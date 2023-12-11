/*
 * Copyright (c) 2024  Alcosi Group Ltd. and affiliates.
 *
 * Portions of this software are licensed as follows:
 *
 *     All content that resides under the "alcosi" and "atomicon" or “deploy” directories of this repository, if that directory exists, is licensed under the license defined in "LICENSE.TXT".
 *
 *     All third-party components incorporated into this software are licensed under the original license provided by the owner of the applicable component.
 *
 *     Content outside of the above-mentioned directories or restrictions above is available under the MIT license as defined below.
 *
 *
 *
 * MIT License
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy of this software and associated documentation files (the "Software"), to deal in the Software without restriction, including without limitation the rights to use, copy, modify, merge, publish, distribute, sublicense, and/or sell copies of the Software, and to permit persons to whom the Software is urnished to do so, subject to the following conditions:
 *
 *
 *
 * The above copyright notice and this permission notice shall be included in all copies or substantial portions of the Software.
 *
 *
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY, FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM, OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE SOFTWARE.
 */

package com.alcosi.lib.crypto.nodes

import com.alcosi.lib.executors.SchedulerTimer
import java.net.URL
import java.time.Duration
import java.time.LocalDateTime
import java.util.concurrent.Callable
import java.util.concurrent.ThreadPoolExecutor
import java.util.concurrent.TimeUnit
import java.util.logging.Level
import java.util.logging.Logger

open class CryptoNodeHealthActualizer(
    val loggingLevel: Level,
    val cryptoNodeProperties: CryptoNodeProperties,
    val executor: ThreadPoolExecutor,
    val cryptoNodeHealthChecker: CryptoNodeHealthChecker,
    val maxRefreshTimeout: Duration,
) {
    val logger = Logger.getLogger(this.javaClass.name)

    protected val serviceListRaw by lazy { getServiceList() }

    protected open fun getServiceList(): List<Service> {
        return cryptoNodeProperties.url.entries.asSequence()
            .filter { it.value != null }
            .flatMap { e ->
                e.value.split(", ").map { URL(it) }.map { Service(e.key, it) }
            }.toList()
    }

    val serviceStatuses = HashMap<Int, List<ServiceStatus>>()

    @JvmRecord
    data class Service(val chainId: Int, val url: URL)

    @JvmRecord
    data class ServiceStatus(
        val chainId: Int,
        val url: URL,
        val status: Boolean,
        val timeout: Long,
        val checkedTime: LocalDateTime = LocalDateTime.now(),
    )

    protected open val scheduler =
        object : SchedulerTimer(cryptoNodeProperties.health.checkDelay, "CheckNodes", loggingLevel) {
            override fun startBatch() {
                val list =
                    serviceListRaw
                        .asSequence()
                        .map { it to executor.submit(Callable { return@Callable cryptoNodeHealthChecker.check(it.url) }) }
                        .map {
                            try {
                                val heathStatus =
                                    it.second.get(
                                        maxRefreshTimeout.toMillis(),
                                        TimeUnit.MILLISECONDS,
                                    )
                                if (loggingLevel != Level.OFF) {
                                    logger.log(loggingLevel, "Health ${it.first.url}:$heathStatus")
                                }
                                it.first to heathStatus
                            } catch (t: Throwable) {
                                logger.log(Level.SEVERE, "Error actualize health ${it.first.url}", t)
                                return@map null
                            }
                        }
                        .filterNotNull()
                        .filter { it.second?.status ?: false }
                        .map { ServiceStatus(it.first.chainId, it.first.url, it.second!!.status, it.second!!.timeout) }
                        .toList()
                list.groupBy { it.chainId }.forEach { serviceStatuses[it.key] = it.value }
            }
        }
}

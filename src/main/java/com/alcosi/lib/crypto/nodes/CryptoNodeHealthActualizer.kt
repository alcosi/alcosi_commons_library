/*
 * Copyright (c) 2023 Alcosi Group Ltd. and affiliates.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 *
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

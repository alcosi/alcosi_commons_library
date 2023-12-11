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

@file:Suppress("ktlint:standard:no-wildcard-imports")

package com.alcosi.lib.crypto.nodes

import com.alcosi.lib.logging.annotations.LogTime
import java.net.URL
import java.time.Duration
import java.util.*
import java.util.concurrent.Callable
import java.util.concurrent.Executors
import java.util.concurrent.Future
import java.util.concurrent.TimeUnit
import java.util.concurrent.atomic.AtomicLong
import java.util.logging.Logger

open class CryptoNodesLoadBalancer(
    val cryptoNodesHealthActualizer: CryptoNodeHealthActualizer,
    val balancerTimeout: Duration,
) {
    val logger = Logger.getLogger(this.javaClass.name)

    val random = Random()
    val sleepTime = Duration.ofMillis(3000)
    protected val executor = Executors.newSingleThreadExecutor()

    @JvmRecord
    data class ServiceChance(
        val url: URL,
        val chance: Long,
        val range: LongRange,
    )

    @LogTime
    fun getActualUrl(
        chainId: Int,
        timeout: Long = 0,
    ): Future<URL> {
        return executor.submit(Callable { internal(chainId, timeout) })
    }

    private fun internal(
        chainId: Int,
        timeout: Long = 0,
    ): URL {
        val time = System.currentTimeMillis()
        val list = cryptoNodesHealthActualizer.serviceStatuses[chainId]?.filter { it.status }.orEmpty()
        if (list.isEmpty()) {
            val sleepMillis = sleepTime.toMillis()
            if (timeout + sleepMillis < balancerTimeout.toMillis()) {
                logger.info("No valid url for chain $chainId. Waiting for it")
                TimeUnit.MILLISECONDS.sleep(sleepMillis)
                return internal(chainId, System.currentTimeMillis() - time)
            } else {
                throw IllegalStateException("Can't get actual url. Timeout.")
            }
        }
        if (list.size == 1) {
            return list.first().url
        }
        val timeoutMax = list.maxOf { it.timeout }
        val atomicPrevRange = AtomicLong(0)
        val prepared =
            list.map {
                val chance = timeoutMax * 2 - it.timeout
                val downRange = atomicPrevRange.getAndAdd(chance)
                val range = downRange.rangeTo(downRange + chance)
                return@map ServiceChance(it.url, chance, range)
            }
        val picked = random.nextLong(atomicPrevRange.get() - 1)
        return prepared.first { it.range.contains(picked) }.url
    }
}

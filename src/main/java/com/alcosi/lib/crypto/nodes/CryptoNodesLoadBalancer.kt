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
    open fun getActualUrl(
        chainId: Int,
        timeout: Long = 0,
    ): Future<URL> {
        return executor.submit(Callable { internal(chainId, timeout) })
    }

    protected open fun internal(
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

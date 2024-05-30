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

/**
 * CryptoNodesLoadBalancer is a class that provides load balancing functionality for crypto node services.
 *
 * @property cryptoNodesHealthActualizer The CryptoNodeHealthActualizer instance used for retrieving the health status of crypto node services.
 * @property balancerTimeout The timeout duration for the load balancer.
 * @property logger The logger instance for logging events and messages.
 * @property random The Random instance for generating random numbers.
 * @property sleepTime The sleep duration for waiting between attempts to get a valid URL.
 * @property executor The ExecutorService instance for running tasks.
 */
open class CryptoNodesLoadBalancer(
    val cryptoNodesHealthActualizer: CryptoNodeHealthActualizer,
    val balancerTimeout: Duration,
) {
    val logger = Logger.getLogger(this.javaClass.name)

    val random = Random()
    val sleepTime = Duration.ofMillis(3000)
    protected val executor = Executors.newSingleThreadExecutor()

    /**
     * Represents a service chance that consists of a URL, chance, and range.
     *
     * @property url The URL associated with the service chance.
     * @property chance The chance of the service being selected.
     * @property range The range of possible values for selection.
     */
    @JvmRecord
    data class ServiceChance(
        val url: URL,
        val chance: Long,
        val range: LongRange,
    )

    /**
     * Retrieves the actual URL for the given chain ID with an optional timeout.
     *
     * @param chainId The chain ID for which to retrieve the actual URL.
     * @param timeout The timeout duration in milliseconds. Default value is 0.
     * @return A Future object representing the result of the URL retrieval.
     */
    @LogTime
    open fun getActualUrl(
        chainId: Int,
        timeout: Long = 0,
    ): Future<URL> {
        return executor.submit(Callable { internal(chainId, timeout) })
    }

    /**
     * Selects an internal URL for a given chain ID based on the health status and timeouts of available URLs.
     *
     * @param chainId The ID of the chain for which to select an internal URL.
     * @param timeout The timeout value in milliseconds. Default is 0.
     * @return The selected internal URL for the specified chain ID.
     * @throws IllegalStateException if no valid URL is available within the timeout period.
     */
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

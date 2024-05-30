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

import io.github.breninsul.javatimerscheduler.registry.SchedulerType
import io.github.breninsul.javatimerscheduler.registry.TaskSchedulerRegistry
import java.net.URL
import java.time.Duration
import java.time.LocalDateTime
import java.util.concurrent.Callable
import java.util.concurrent.ExecutorService
import java.util.concurrent.TimeUnit
import java.util.logging.Level
import java.util.logging.Logger

/**
 * CryptoNodeHealthActualizer class is responsible for periodically checking the health
 * of various crypto node services and updating their statuses accordingly.
 *
 * @property loggingLevel The logging level for the class.
 * @property cryptoNodeProperties The properties related to the crypto node services.
 * @property executor The executor service for running health check tasks.
 * @property cryptoNodeHealthChecker The health checker for crypto node services.
 * @property maxRefreshTimeout The maximum refresh timeout duration.
 */
open class CryptoNodeHealthActualizer(
    val loggingLevel: Level,
    val cryptoNodeProperties: CryptoNodeProperties,
    val executor: ExecutorService,
    val cryptoNodeHealthChecker: CryptoNodeHealthChecker,
    val maxRefreshTimeout: Duration,
) {
    /**
     * The logger variable is an instance of the Logger class, used for logging events and messages.
     */
    val logger = Logger.getLogger(this.javaClass.name)
    init {
        TaskSchedulerRegistry.registerTypeTask(SchedulerType.VIRTUAL_WAIT, "CheckNodes", cryptoNodeProperties.health.checkDelay, cryptoNodeProperties.health.firstDelay, this::class, loggingLevel) { checkNodes() }
    }
    /**
     * Lazily-initialized variable that holds the raw service list.
     *
     * The serviceListRaw variable is lazily initialized using the "by lazy" delegated property. It is a protected
     * variable, accessible within the class and its subclasses. The value is obtained by calling the getServiceList()
     * function.
     *
     * @return The raw service list.
     */
    protected val serviceListRaw by lazy { getServiceList() }

    /**
     * Retrieves a list of service objects based on the provided URL entries in the cryptoNodeProperties.
     *
     * @return The list of Service objects obtained from the URL entries.
     */
    protected open fun getServiceList(): List<Service> {
        return cryptoNodeProperties.url.entries.asSequence()
            .filter { it.value != null }
            .flatMap { e ->
                e.value.split(", ").map { URL(it) }.map { Service(e.key, it) }
            }.toList()
    }

    /**
     * Represents a map of service statuses.
     *
     * The key of the map represents the service ID, and the associated value
     * is a list of ServiceStatus objects representing the statuses of the service.
     *
     * @property serviceStatuses The map of service statuses, where the key is the service ID
     *            and the value is a list of ServiceStatus objects.
     */
    val serviceStatuses = HashMap<Int, List<ServiceStatus>>()

    /**
     * Represents a service with a chain ID and a URL.
     *
     * @property chainId The chain ID associated with the service.
     * @property url The URL of the service.
     */
    @JvmRecord
    data class Service(val chainId: Int, val url: URL)

    /**
     * Represents the status of a service.
     *
     * @property chainId The chain ID of the service.
     * @property url The URL of the service.
     * @property status The status of the service.
     * @property timeout*/
    @JvmRecord
    data class ServiceStatus(
        val chainId: Int,
        val url: URL,
        val status: Boolean,
        val timeout: Long,
        val checkedTime: LocalDateTime = LocalDateTime.now(),
    )

    /**
     * Checks the health status of the nodes in the service list.
     */
    protected open fun checkNodes() {
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

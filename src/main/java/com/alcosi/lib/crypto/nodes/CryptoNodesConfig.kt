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

import com.alcosi.lib.executors.NormalThreadPoolExecutor
import com.alcosi.lib.filters.servlet.HeaderHelper
import com.alcosi.lib.logging.http.okhttp.OKLoggingInterceptor
import io.github.breninsul.okhttp.logging.OkHttpLoggerConfiguration
import okhttp3.OkHttpClient
import org.springframework.beans.factory.annotation.Qualifier
import org.springframework.boot.autoconfigure.AutoConfiguration
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty
import org.springframework.boot.context.properties.EnableConfigurationProperties
import org.springframework.context.annotation.Bean
import org.springframework.scheduling.annotation.Scheduled
import org.web3j.protocol.admin.Admin
import org.web3j.tx.gas.ContractGasProvider
import org.web3j.tx.gas.DefaultGasProvider
import java.time.Duration
import java.util.concurrent.ExecutorService
import java.util.concurrent.ScheduledThreadPoolExecutor

/**
 * This class represents the configuration for Crypto Nodes.
 *
 * It is annotated with `@ConditionalOnClass` and `@ConditionalOnProperty` to ensure that the class is loaded
 * only if the required classes and properties are present. It is also annotated with `@EnableConfigurationProperties`
 * to enable the usage of `CryptoNodeProperties` as a configuration property class.
 *
 * The class contains several bean methods annotated with `@Bean` to create and configure beans.
 *
 * @property cryptoNodeProperties The configuration properties for Crypto Nodes.
 * @constructor Creates a CryptoNodesConfig instance.
 */
@ConditionalOnClass(Scheduled::class, Admin::class)
@ConditionalOnProperty(
    prefix = "common-lib.crypto.node",
    name = ["disabled"],
    matchIfMissing = true,
    havingValue = "false",
)
@EnableConfigurationProperties(CryptoNodeProperties::class)
@AutoConfiguration
class CryptoNodesConfig {
    /**
     * Creates an instance of OkHttpClient with the given parameters.
     *
     * @param cryptoNodeProperties The instance of CryptoNodeProperties.
     * @param headerHelper The instance of HeaderHelper.
     * @return The created OkHttpClient with configured timeouts and logging interceptor.
     */
    @Bean("cryptoNodeHttpClient")
    fun createOkHttpClient(
        cryptoNodeProperties: CryptoNodeProperties,
        headerHelper: HeaderHelper,
    ): OkHttpClient {
        val builder = OkHttpClient.Builder()
        configureTimeouts(
            builder,
            OKLoggingInterceptor(cryptoNodeProperties.logging, headerHelper),
            cryptoNodeProperties.nodesTimeout,
        )
        return builder.build()
    }

    /**
     * Configures the timeouts for the OkHttpClient builder using the specified nodeTimeout duration.
     *
     * @param builder The OkHttpClient builder to configure timeouts for.
     * @param interceptor The OKLoggingInterceptor instance to add as an interceptor.
     * @param nodeTimeout The duration for the timeouts.
     */
    protected open fun configureTimeouts(
        builder: OkHttpClient.Builder,
        interceptor: OKLoggingInterceptor,
        nodeTimeout: Duration,
    ) {
        builder.connectTimeout(nodeTimeout)
        builder.readTimeout(nodeTimeout)
        builder.writeTimeout(nodeTimeout)
        builder.addInterceptor(interceptor)
    }

    /**
     * Returns the default gas provider for the contract.
     *
     * @return The ContractGasProvider instance.
     */
    @Bean
    @ConditionalOnMissingBean(ContractGasProvider::class)
    fun gasProvider(): ContractGasProvider = DefaultGasProvider()

    /**
     * Retrieves a HealthCheckerNormalThreadPoolExecutor instance with the given CryptoNodeProperties.
     *
     * @param cryptoNodeProperties The properties related to the crypto node services.
     *
     * @return The HealthCheckerNormalThreadPoolExecutor instance.
     */
    @Bean("healthCheckerNormalThreadPoolExecutor")
    fun getHealthCheckerNormalThreadPoolExecutor(cryptoNodeProperties: CryptoNodeProperties): ExecutorService = NormalThreadPoolExecutor.build(cryptoNodeProperties.health.threads, "crypto-health-check")

    /**
     * Retrieves the CryptoNodeHealthActualizer instance.
     *
     * @param cryptoNodeProperties The properties related to the crypto node services.
     * @param executor The executor service for running health check tasks.
     * @param httpClient The OkHttpClient for making HTTP requests.
     * @return The CryptoNodeHealthActualizer instance
     */
    @Bean
    @ConditionalOnMissingBean(CryptoNodeHealthActualizer::class)
    fun getCryptoNodeHealthActualizer(
        cryptoNodeProperties: CryptoNodeProperties,
        @Qualifier("healthCheckerNormalThreadPoolExecutor") executor: ExecutorService,
        @Qualifier("cryptoNodeHttpClient") httpClient: OkHttpClient,
    ): CryptoNodeHealthActualizer =
        CryptoNodeHealthActualizer(
            cryptoNodeProperties.health.nodesLoggingLevel.javaLevel,
            cryptoNodeProperties,
            executor,
            CryptoNodeHealthChecker(httpClient),
            cryptoNodeProperties.health.refreshTimeout,
        )

    /**
     * Returns the default gas provider for contracts.
     *
     * If there is no existing bean of type `ContractGasProvider`, this method will return a new instance of `DefaultGasProvider`.
     *
     * @return the default gas provider
     */
    @ConditionalOnMissingBean(ContractGasProvider::class)
    fun getDefaultGasProvider(): ContractGasProvider = DefaultGasProvider()

    /**
     * Generates the configuration for Crypto Nodes admin service.
     *
     * @param properties The properties for Crypto Nodes.
     * @param httpClient The OkHttpClient for making HTTP requests.
     * @param cryptoNodesLoadBalancer The load balancer for selecting the actual URL of the Crypto Node.
     * @return The holder for the admin services with the generated configuration.
     */
    @Bean
    @ConditionalOnMissingBean(CryptoNodesAdminServiceHolder::class)
    fun genNodesConfig(
        properties: CryptoNodeProperties,
        @Qualifier("cryptoNodeHttpClient") httpClient: OkHttpClient,
        cryptoNodesLoadBalancer: CryptoNodesLoadBalancer,
    ): CryptoNodesAdminServiceHolder {
        val map: MutableMap<Int, Admin> = HashMap()
        val url = properties.url ?: emptyMap()
        url
            .forEach { (key, value) ->
                map[key] =
                    Admin.build(
                        CryptoNodeLoadBalancedHttpService(key, cryptoNodesLoadBalancer, httpClient),
                        properties.poolingInterval.toMillis(),
                        ScheduledThreadPoolExecutor(
                            properties.threads!!,
                        ),
                    )
            }
        return CryptoNodesAdminServiceHolder(map)
    }

    /**
     * Retrieves the CryptoNodesLoadBalancer instance.
     *
     * @param cryptoNodesHealthActualizer The CryptoNodeHealthActualizer instance.
     * @param properties The CryptoNodeProperties instance.
     * @return The CryptoNodesLoadBalancer instance.
     */
    @Bean
    @ConditionalOnMissingBean(CryptoNodesLoadBalancer::class)
    fun getCryptoNodesLoadBalancer(
        cryptoNodesHealthActualizer: CryptoNodeHealthActualizer,
        properties: CryptoNodeProperties,
    ): CryptoNodesLoadBalancer = CryptoNodesLoadBalancer(cryptoNodesHealthActualizer, properties.balancerTimeout)
}

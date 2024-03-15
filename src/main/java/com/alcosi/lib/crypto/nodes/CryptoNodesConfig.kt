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
import java.util.concurrent.ScheduledThreadPoolExecutor
import java.util.concurrent.ThreadPoolExecutor
import java.util.logging.Level

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
    @Bean("cryptoNodeHttpClient")
    fun createOkHttpClient(
        cryptoNodeProperties: CryptoNodeProperties,
        headerHelper: HeaderHelper,
    ): OkHttpClient {
        val builder = OkHttpClient.Builder()
        configureTimeouts(
            builder,
            OKLoggingInterceptor(
                cryptoNodeProperties.nodesLoggingMaxBody,
                Level.parse(cryptoNodeProperties.nodesLoggingLevel),
                headerHelper,
                0,
            ),
            cryptoNodeProperties.nodesTimeout,
        )
        return builder.build()
    }

    protected fun configureTimeouts(
        builder: OkHttpClient.Builder,
        interceptor: OKLoggingInterceptor,
        nodeTimeout: Duration,
    ) {
        builder.connectTimeout(nodeTimeout)
        builder.readTimeout(nodeTimeout)
        builder.writeTimeout(nodeTimeout)
        builder.addInterceptor(interceptor)
    }

    @Bean
    @ConditionalOnMissingBean(ContractGasProvider::class)
    fun gasProvider(): ContractGasProvider {
        return DefaultGasProvider()
    }

    @Bean("healthCheckerNormalThreadPoolExecutor")
    fun getHealthCheckerNormalThreadPoolExecutor(cryptoNodeProperties: CryptoNodeProperties): NormalThreadPoolExecutor {
        return NormalThreadPoolExecutor.build(cryptoNodeProperties.health.threads, "crypto-health-check", Duration.ofDays(1))
    }

    @Bean
    @ConditionalOnMissingBean(CryptoNodeHealthActualizer::class)
    fun getCryptoNodeHealthActualizer(
        cryptoNodeProperties: CryptoNodeProperties,
        @Qualifier("healthCheckerNormalThreadPoolExecutor") executor: ThreadPoolExecutor,
        @Qualifier("cryptoNodeHttpClient") httpClient: OkHttpClient,
    ): CryptoNodeHealthActualizer {
        return CryptoNodeHealthActualizer(
            Level.parse(cryptoNodeProperties.health.nodesLoggingLevel),
            cryptoNodeProperties,
            executor,
            CryptoNodeHealthChecker(httpClient),
            cryptoNodeProperties.health.refreshTimeout,
        )
    }

    @ConditionalOnMissingBean(ContractGasProvider::class)
    fun getDefaultGasProvider(): ContractGasProvider {
        return DefaultGasProvider()
    }

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

    @Bean
    @ConditionalOnMissingBean(CryptoNodesLoadBalancer::class)
    fun getCryptoNodesLoadBalancer(
        cryptoNodesHealthActualizer: CryptoNodeHealthActualizer,
        properties: CryptoNodeProperties,
    ): CryptoNodesLoadBalancer {
        return CryptoNodesLoadBalancer(cryptoNodesHealthActualizer, properties.balancerTimeout)
    }
}

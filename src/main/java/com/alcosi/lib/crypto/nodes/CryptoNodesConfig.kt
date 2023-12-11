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

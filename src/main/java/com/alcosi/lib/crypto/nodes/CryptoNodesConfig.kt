/*
 * Copyright (c) 2023  Alcosi Group Ltd. and affiliates.
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


import okhttp3.OkHttpClient
import org.springframework.beans.factory.annotation.Qualifier
import org.springframework.beans.factory.annotation.Value
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty
import org.springframework.boot.autoconfigure.condition.ConditionalOnSingleCandidate
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.scheduling.annotation.Scheduled
import org.web3j.protocol.admin.Admin
import org.web3j.tx.gas.ContractGasProvider
import org.web3j.tx.gas.DefaultGasProvider
import com.alcosi.lib.executors.NormalThreadPoolExecutor
import com.alcosi.lib.logging.http.okhttp.OKLoggingInterceptor
import java.time.Duration
import java.util.concurrent.ScheduledThreadPoolExecutor
import java.util.concurrent.ThreadPoolExecutor
import java.util.logging.Level

@ConditionalOnClass(Scheduled::class,Admin::class)
@ConditionalOnProperty(
    prefix = "common-lib.crypto.admins",
    name = arrayOf("disabled"),
    matchIfMissing = true,
    havingValue = "false"
)
@Configuration
open class CryptoNodesConfig(
    val configMap: CryptoNodeProperties,
) {

    @Bean("cryptoNodeHttpClient")
    fun createOkHttpClient(
        @Value("\${common-lib.request_body_log.max.ok_client_nodes:10000}") maxBodySize: Int,
        @Value("\${common-lib.crypto.node.timeout:15s}") nodeTimeout: Duration,
        @Value("\${common-lib.logging.level.nodes_http:INFO}") loggingLevel: String,
    ): OkHttpClient {
        val builder = OkHttpClient.Builder()
        configureTimeouts(builder, OKLoggingInterceptor(maxBodySize, Level.parse(loggingLevel)), nodeTimeout)
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
fun getHealthCheckerNormalThreadPoolExecutor(   @Value("\${common-lib.crypto.node.balancer.health_check_executor_threads:20}") executorThreads: Int,):NormalThreadPoolExecutor{
    return NormalThreadPoolExecutor.build(executorThreads,"crypto-health-check", Duration.ofDays(1))
}
    @Bean
    fun getCryptoNodeHealthActualizer(
        @Value("\${common-lib.logging.level.nodes_health_stats:INFO}") loggingLevel: String,
        @Qualifier("healthCheckerNormalThreadPoolExecutor") executor: ThreadPoolExecutor,
        @Qualifier("cryptoNodeHttpClient") httpClient: OkHttpClient,
        @Value("\${common-lib.crypto.node.timeout.refresh:10s}") refreshTimeout: Duration,
        ):CryptoNodeHealthActualizer{
        return                     CryptoNodeHealthActualizer(Level.parse(loggingLevel),configMap, executor, CryptoNodeHealthChecker(httpClient), refreshTimeout)
    }
    @ConditionalOnSingleCandidate(ContractGasProvider::class)
    fun getDefaultGasProvider():ContractGasProvider{
        return DefaultGasProvider()
    }
    @Bean
    fun genNodesConfig(
        @Value("\${common-lib.crypto.node.pooling.interval:15s}") poolingInterval: Duration,
        @Value("\${common-lib.crypto.node.threads:20}") threads: Int?,
        @Value("\${common-lib.crypto.node.timeout.balancer:10s}") balancerTimeout: Duration,
        @Qualifier("cryptoNodeHttpClient") httpClient: OkHttpClient,
        cryptoNodesLoadBalancer:CryptoNodesLoadBalancer
    ): CryptoNodesAdminServiceHolder {
        val map: MutableMap<Int, Admin> = HashMap()
        val url = configMap.url ?: emptyMap()
        url
            .forEach { (key, value) ->
                map[key] = Admin.build(
                    CryptoNodeLoadBalancedHttpService(key,cryptoNodesLoadBalancer ,httpClient),
                    poolingInterval.toMillis(),
                    ScheduledThreadPoolExecutor(
                        threads!!
                    )
                )
            }
        return CryptoNodesAdminServiceHolder(map)
    }
}
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

package com.alcosi.lib.logging.http.okhttp

import com.alcosi.lib.filters.servlet.HeaderHelper
import com.alcosi.lib.logging.http.OrderedComparator
import okhttp3.Interceptor
import okhttp3.OkHttpClient
import org.springframework.beans.factory.ObjectProvider
import org.springframework.boot.autoconfigure.AutoConfiguration
import org.springframework.boot.autoconfigure.condition.ConditionalOnBean
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty
import org.springframework.boot.context.properties.EnableConfigurationProperties
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Primary
import java.util.logging.Level

@AutoConfiguration
@ConditionalOnClass(Interceptor::class)
@EnableConfigurationProperties(OkHttpLoggingProperties::class)
@ConditionalOnProperty(prefix = "common-lib.okhttp", name = ["disabled"], matchIfMissing = true, havingValue = "false")
class OkHttpConfig {
    @Bean
    @ConditionalOnMissingBean(OKLoggingInterceptor::class)
    @ConditionalOnBean(HeaderHelper::class)
    @ConditionalOnProperty(prefix = "common-lib.okhttp", name = ["logging-disabled"], matchIfMissing = true, havingValue = "false")
    fun getOKLoggingInterceptor(
        properties: OkHttpLoggingProperties,
        headerHelper: HeaderHelper,
    ): OKLoggingInterceptor {
        return OKLoggingInterceptor(properties.maxLogBodySize, Level.parse(properties.loggingLevel), headerHelper, 1)
    }

    @Bean
    @ConditionalOnMissingBean(OKContextHeadersInterceptor::class)
    @ConditionalOnBean(HeaderHelper::class)
    @ConditionalOnProperty(prefix = "common-lib.okhttp", name = ["context-headers-disabled"], matchIfMissing = true, havingValue = "false")
    fun getOKContextInterceptor(headerHelper: HeaderHelper): OKContextHeadersInterceptor {
        return OKContextHeadersInterceptor(headerHelper, 0)
    }

    @Bean("okHttpClient")
    @Primary
    fun createOkHttpClient(
        properties: OkHttpLoggingProperties,
        headerHelper: HeaderHelper,
        interceptors: ObjectProvider<Interceptor>,
    ): OkHttpClient {
        val builder = OkHttpClient.Builder()
        configureTimeouts(
            builder,
            interceptors.toList().sortedWith(OrderedComparator),
            properties,
        )
        return builder.build()
    }

    protected fun configureTimeouts(
        builder: OkHttpClient.Builder,
        interceptors: List<Interceptor>,
        properties: OkHttpLoggingProperties,
    ) {
        builder.connectTimeout(properties.connectTimeout)
        builder.readTimeout(properties.readTimeout)
        builder.writeTimeout(properties.writeTimeout)
        interceptors.forEach { interceptor ->
            builder.addInterceptor(interceptor)
        }
    }
}

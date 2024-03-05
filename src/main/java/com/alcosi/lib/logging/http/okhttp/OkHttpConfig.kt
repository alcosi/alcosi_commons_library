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

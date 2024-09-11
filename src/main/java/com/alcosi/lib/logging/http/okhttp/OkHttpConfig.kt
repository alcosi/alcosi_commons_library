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
import io.github.breninsul.okhttp.logging.OkHttpLoggerConfiguration
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

/**
 * The OkHttpConfig class is a configuration class that provides beans related to OkHttp library.
 *
 * It is annotated with [AutoConfiguration], [ConditionalOnClass], and [EnableConfigurationProperties] annotations
 * to ensure that it is automatically configured when the `Interceptor` class is available in the classpath,
 * and the configuration properties class `OkHttpLoggingProperties` is enabled.
 *
 *
 * @see OKLoggingInterceptor
 *
 * @property properties The configuration properties for OkHttp.
 * @property headerHelper An instance of the HeaderHelper class.
 */
@AutoConfiguration
@ConditionalOnClass(Interceptor::class)
@EnableConfigurationProperties(OkHttpLoggingProperties::class)
@ConditionalOnProperty(prefix = "common-lib.okhttp", name = ["enabled"], matchIfMissing = true, havingValue = "true")
class OkHttpConfig {
    /**
     * Retrieves an instance of the OKLoggingInterceptor class.
     *
     * @param properties The OkHttpLoggingProperties object that holds the logging configuration properties.
     */
    @Bean
    @ConditionalOnMissingBean(OKLoggingInterceptor::class)
    @ConditionalOnBean(HeaderHelper::class)
    @ConditionalOnProperty(prefix = "common-lib.okhttp.logging", name = ["enabled"], matchIfMissing = true, havingValue = "true")
    fun getOKLoggingInterceptor(
        properties: OkHttpLoggingProperties,
        headerHelper: HeaderHelper,
    ): OKLoggingInterceptor {
        val config = OkHttpLoggerConfiguration()
        val requestMaskers =
            listOf(
                config.okHttpRequestRegexJsonBodyMasking(properties.logging.request.mask),
                config.okHttpRequestFormUrlencodedBodyMasking(properties.logging.request.mask),
            )
        val responseMaskers =
            listOf(
                config.okHttpResponseRegexJsonBodyMasking(properties.logging.request.mask),
                config.okHttpResponseFormUrlencodedBodyMasking(properties.logging.request.mask),
            )
        val uriMaskers = listOf(config.okHttpUriMaskingDelegate(properties.logging.request.mask))
        return OKLoggingInterceptor(properties.logging, uriMaskers, requestMaskers, responseMaskers, headerHelper)
    }

    /**
     * Retrieves an instance of the OKContextHeadersInterceptor class.
     *
     * @param headerHelper The HeaderHelper object used to create request headers.
     **/
    @Bean
    @ConditionalOnMissingBean(OKContextHeadersInterceptor::class)
    @ConditionalOnBean(HeaderHelper::class)
    @ConditionalOnProperty(prefix = "common-lib.okhttp", name = ["context-headers-disabled"], matchIfMissing = true, havingValue = "false")
    fun getOKContextInterceptor(headerHelper: HeaderHelper): OKContextHeadersInterceptor = OKContextHeadersInterceptor(headerHelper, 0)

    /**
     * Creates an instance of OkHttpClient with the provided configurations.
     *
     * @param properties The OkHttpLoggingProperties*/
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

    /**
     * Configures the timeouts for the OkHttpClient builder.
     *
     * @param builder        The OkHttpClient.Builder object to configure.
     * @param interceptors   The list of Interceptors to add to the builder.
     * @param properties     The OkHttpLoggingProperties object that holds the logging configuration properties.
     */
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

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

package com.alcosi.lib.filters.servlet.log

import com.alcosi.lib.filters.servlet.FilterConfig
import com.alcosi.lib.filters.servlet.ServletFilterProperties
import com.alcosi.lib.filters.servlet.ThreadContext
import org.springframework.boot.autoconfigure.AutoConfigureAfter
import org.springframework.boot.autoconfigure.condition.ConditionalOnBean
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty
import org.springframework.boot.autoconfigure.web.servlet.ConditionalOnMissingFilterBean
import org.springframework.boot.context.properties.EnableConfigurationProperties
import org.springframework.boot.web.servlet.FilterRegistrationBean
import org.springframework.boot.web.servlet.server.ServletWebServerFactory
import org.springframework.context.annotation.Bean
import java.util.logging.Level

/**
 * The LoggingFilterConfig class is responsible for configuring and creating beans related to the logging filter.
 * It is annotated with @AutoConfigureAfter(FilterConfig::class) to ensure that it is configured after the FilterConfig class.
 *
 * The class is also annotated with @ConditionalOnBean and @ConditionalOnProperty, which check if the FilterConfig class is present and
 * the property "common-lib.filter.logging.disabled" is set to false (default is true), respectively.
 *
 * The class is also annotated with @EnableConfigurationProperties(LoggingFilterProperties::class) to enable the use of LoggingFilterProperties.
 */
@AutoConfigureAfter(FilterConfig::class)
@ConditionalOnBean(FilterConfig::class)
@ConditionalOnProperty(
    prefix = "common-lib.filter.logging",
    name = ["disabled"],
    matchIfMissing = true,
    havingValue = "false",
)
@EnableConfigurationProperties(LoggingFilterProperties::class)
open class LoggingFilterConfig {
    /**
     * Returns an instance of `LoggingFilter.LogInternalService` that is used to configure logging properties for internal services.
     *
     * @param properties The `LoggingFilterProperties` object containing logging configuration properties.
     * @param threadContext The `ThreadContext` object providing thread-local data.
     * @return An instance of `LoggingFilter.LogInternalService` configured with the specified logging level, maximum body size for logs, and thread context.
     */
    @Bean(name = ["logInternalService"], value = ["logInternalService"])
    fun logInternalService(
        properties: LoggingFilterProperties,
        threadContext: ThreadContext,
    ): LoggingFilter.LogInternalService {
        return LoggingFilter.LogInternalService(Level.parse(properties.loggingLevel), properties.maxBodySizeLog, threadContext)
    }

    /**
     * Creates a [FilterRegistrationBean] for the [LoggingFilter].
     *
     * @param properties The [LoggingFilterProperties] object containing the filter properties.
     * @param servletProperties The [ServletFilterProperties] object containing the servlet properties.
     * @param logInternalService The [LoggingFilter.LogInternalService] object for internal logging.
     * @param threadContext The [ThreadContext] object for thread context management.
     * @return A [FilterRegistrationBean] configured with the [LoggingFilter].
     */
    @Bean(name = ["loggingFilterBean"], value = ["loggingFilterBean"])
    @ConditionalOnClass(ServletWebServerFactory::class)
    @ConditionalOnMissingFilterBean(LoggingFilter::class)
    @ConditionalOnMissingBean(LoggingFilter::class)
    fun loggingFilter(
        properties: LoggingFilterProperties,
        servletProperties: ServletFilterProperties,
        logInternalService: LoggingFilter.LogInternalService,
        threadContext: ThreadContext,
    ): FilterRegistrationBean<LoggingFilter> {
        val registrationBean = FilterRegistrationBean<LoggingFilter>()
        registrationBean.filter = LoggingFilter(logInternalService, threadContext, properties.maxBodySize)
        registrationBean.order = servletProperties.baseOrder + properties.orderDelta
        return registrationBean
    }
}

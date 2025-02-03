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
import io.github.breninsul.servlet.logging2.ServletLoggerConfiguration
import io.github.breninsul.servlet.logging2.ServletLoggerProperties
import io.github.breninsul.servlet.logging2.filter.ServletLoggingFilter
import org.springframework.boot.autoconfigure.AutoConfigureAfter
import org.springframework.boot.autoconfigure.AutoConfigureBefore
import org.springframework.boot.autoconfigure.condition.ConditionalOnBean
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty
import org.springframework.boot.autoconfigure.web.servlet.ConditionalOnMissingFilterBean
import org.springframework.boot.context.properties.EnableConfigurationProperties
import org.springframework.boot.web.servlet.FilterRegistrationBean
import org.springframework.boot.web.servlet.server.ServletWebServerFactory
import org.springframework.context.annotation.Bean
import org.springframework.web.servlet.HandlerMapping

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
@AutoConfigureBefore(ServletLoggerConfiguration::class)
@ConditionalOnBean(FilterConfig::class)
@ConditionalOnProperty(
    prefix = "common-lib.filter.logging",
    name = ["enabled"],
    matchIfMissing = true,
    havingValue = "true",
)
@EnableConfigurationProperties(LoggingFilterProperties::class)
open class LoggingFilterConfig {


    @Bean(name = ["ServletLoggerService"])
    @ConditionalOnClass(ServletWebServerFactory::class)
    @ConditionalOnMissingBean(AlcosiServletLoggerService::class)
    fun loggingService(
        properties: LoggingFilterProperties,
        threadContext: ThreadContext,
    ): AlcosiServletLoggerService {
        return AlcosiServletLoggerService(properties,threadContext)
    }

    @Bean(name = ["ServletLoggingFilter"])
    @ConditionalOnClass(ServletWebServerFactory::class)
    @ConditionalOnMissingFilterBean(AlcosiServletLoggingFilter::class)
    @ConditionalOnMissingBean(AlcosiServletLoggingFilter::class)
    fun loggingFilter(
        properties: LoggingFilterProperties,
        servletProperties: ServletFilterProperties,
        loggerService: AlcosiServletLoggerService,
        handlerMappings: List<HandlerMapping>,
    ): FilterRegistrationBean<ServletLoggingFilter> {
        val registrationBean = FilterRegistrationBean<ServletLoggingFilter>()
        registrationBean.filter = AlcosiServletLoggingFilter(loggerService,properties,handlerMappings)
        registrationBean.order = servletProperties.baseOrder + properties.orderDelta
        return registrationBean
    }
}

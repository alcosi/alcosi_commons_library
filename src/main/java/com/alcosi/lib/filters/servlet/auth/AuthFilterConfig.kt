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

package com.alcosi.lib.filters.servlet.auth

import com.alcosi.lib.filters.servlet.FilterConfig
import com.alcosi.lib.filters.servlet.HeaderHelper
import com.alcosi.lib.filters.servlet.ServletFilterProperties
import com.fasterxml.jackson.databind.ObjectMapper
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

/**
 * AuthFilterConfig is a configuration class responsible for registering and configuring the `AuthFilter` as a servlet filter.
 * It is annotated with @AutoConfigureAfter(FilterConfig::class) to ensure that it is configured after the FilterConfig class.
 * It is also annotated with @ConditionalOnBean(FilterConfig::class) which checks if the FilterConfig bean is present in the application context.
 *
 * The class is annotated with @ConditionalOnProperty to check if the property "common-lib.filter.auth.disabled" is set to false (default is true).
 * If the property is true or not present, the configuration is not enabled.
 *
 * It is annotated with @EnableConfigurationProperties(AuthFilterProperties::class) to enable the configuration properties for the AuthFilter.
 */
@AutoConfigureAfter(FilterConfig::class)
@ConditionalOnBean(FilterConfig::class)
@ConditionalOnProperty(
    prefix = "common-lib.filter.auth",
    name = ["disabled"],
    matchIfMissing = true,
    havingValue = "false",
)
@EnableConfigurationProperties(AuthFilterProperties::class)
open class AuthFilterConfig {
    /**
     * Registers and configures the `AuthFilter` as a servlet filter.
     *
     * @param properties The configuration properties for the `AuthFilter`.
     * @param servletFilterProperties The configuration properties for the servlet filter.
     * @param mapper The instance of the `ObjectMapper` used for serializing/deserializing JSON.
     * @param headerHelper The instance of the `HeaderHelper` used for handling request headers.
     * @return A `FilterRegistrationBean` object representing the registered `AuthFilter`.
     */
    @Bean(name = ["authFilterBean"], value = ["authFilterBean"])
    @ConditionalOnClass(ServletWebServerFactory::class)
    @ConditionalOnMissingFilterBean(AuthFilter::class)
    @ConditionalOnMissingBean(AuthFilter::class)
    fun authFilter(
        properties: AuthFilterProperties,
        servletFilterProperties: ServletFilterProperties,
        mapper: ObjectMapper,
        headerHelper: HeaderHelper,
    ): FilterRegistrationBean<AuthFilter> {
        val registrationBean = FilterRegistrationBean<AuthFilter>()
        registrationBean.filter =
            AuthFilter(
                properties.accessKey,
                headerHelper,
                mapper,
                properties.wrongEnvErrorCode,
                properties.wrongAccessKeyErrorCode,
                properties.noAccessKeyErrorCode,
            )
        registrationBean.order = servletFilterProperties.baseOrder + properties.orderDelta
        return registrationBean
    }
}

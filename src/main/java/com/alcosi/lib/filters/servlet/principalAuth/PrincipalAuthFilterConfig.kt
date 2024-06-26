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

package com.alcosi.lib.filters.servlet.principalAuth

import com.alcosi.lib.filters.servlet.FilterConfig
import com.alcosi.lib.filters.servlet.ServletFilterProperties
import com.alcosi.lib.filters.servlet.ThreadContext
import com.alcosi.lib.secured.encrypt.SensitiveComponent
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
 * The PrincipalAuthFilterConfig class is responsible for configuring and creating a bean of type PrincipalAuthFilter.
 * It is annotated with @AutoConfigureAfter and @ConditionalOnBean, ensuring that it is automatically configured after
 * the FilterConfig class is present in the application context.
 *
 * The class is also annotated with @ConditionalOnProperty, which checks whether the property "common-lib.filter.principal-auth.disabled"
 * is set to false (default is true).
 *
 * The class is also annotated with @EnableConfigurationProperties, which enables the injection of PrincipalAuthFilterProperties into
 * PrincipalAuthFilterConfig.
 */
@AutoConfigureAfter(FilterConfig::class)
@ConditionalOnBean(FilterConfig::class)
@ConditionalOnProperty(
    prefix = "common-lib.filter.principal-auth",
    name = ["disabled"],
    matchIfMissing = true,
    havingValue = "false",
)
@EnableConfigurationProperties(PrincipalAuthFilterProperties::class)
open class PrincipalAuthFilterConfig {
    /**
     * Creates a FilterRegistrationBean for the PrincipalAuthFilter.
     *
     * @param properties The PrincipalAuthFilterProperties object containing the filter properties.
     * @param servletProperties The ServletFilterProperties object containing servlet filter properties.
     * @param sensitiveComponent The SensitiveComponent object used for handling sensitive data.
     * @param mappingHelper The ObjectMapper object used for mapping user details or account details.
     * @param threadContext The ThreadContext object used for setting the authentication principal.
     * @return The created FilterRegistrationBean for the PrincipalAuthFilter.
     */
    @Bean(name = ["principalAuthFilter"], value = ["principalAuthFilter"])
    @ConditionalOnClass(ServletWebServerFactory::class)
    @ConditionalOnMissingFilterBean(PrincipalAuthFilter::class)
    @ConditionalOnMissingBean(PrincipalAuthFilter::class)
    fun principalAuthFilter(
        properties: PrincipalAuthFilterProperties,
        servletProperties: ServletFilterProperties,
        sensitiveComponent: SensitiveComponent,
        mappingHelper: ObjectMapper,
        threadContext: ThreadContext,
    ): FilterRegistrationBean<PrincipalAuthFilter> {
        val registrationBean = FilterRegistrationBean<PrincipalAuthFilter>()
        registrationBean.filter = PrincipalAuthFilter(mappingHelper, threadContext, sensitiveComponent)
        registrationBean.order = servletProperties.baseOrder + properties.orderDelta
        return registrationBean
    }
}

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

package com.alcosi.lib.filters.servlet.principalAuth

import com.alcosi.lib.filters.servlet.FilterConfig
import com.alcosi.lib.filters.servlet.ServletFilterProperties
import com.alcosi.lib.filters.servlet.ThreadContext
import com.alcosi.lib.objectMapper.MappingHelper
import com.alcosi.lib.secured.encrypt.SensitiveComponent
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
    @Bean(name = ["principalAuthFilter"], value = ["principalAuthFilter"])
    @ConditionalOnClass(ServletWebServerFactory::class)
    @ConditionalOnMissingFilterBean(PrincipalAuthFilter::class)
    @ConditionalOnMissingBean(PrincipalAuthFilter::class)
    fun principalAuthFilter(
        properties: PrincipalAuthFilterProperties,
        servletProperties: ServletFilterProperties,
        sensitiveComponent: SensitiveComponent,
        mappingHelper: MappingHelper,
        threadContext: ThreadContext,
    ): FilterRegistrationBean<PrincipalAuthFilter> {
        val registrationBean = FilterRegistrationBean<PrincipalAuthFilter>()
        registrationBean.filter = PrincipalAuthFilter(mappingHelper, threadContext, sensitiveComponent)
        registrationBean.order = servletProperties.baseOrder + properties.orderDelta
        return registrationBean
    }
}

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

package com.alcosi.lib.filters.servlet.context

import com.alcosi.lib.filters.servlet.FilterConfig
import com.alcosi.lib.filters.servlet.HeaderHelper
import com.alcosi.lib.filters.servlet.ServletFilterProperties
import com.alcosi.lib.filters.servlet.ThreadContext
import com.alcosi.lib.objectMapper.MappingHelper
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
    prefix = "common-lib.filter.context",
    name = ["disabled"],
    matchIfMissing = true,
    havingValue = "false",
)
@ConditionalOnClass(ServletWebServerFactory::class)
@EnableConfigurationProperties(ContextFilterProperties::class)
open class ContextFilterConfig {
    @Bean(name = ["contextFilterBean"], value = ["contextFilterBean"])
    @ConditionalOnMissingFilterBean(ContextFilter::class)
    @ConditionalOnMissingBean(ContextFilter::class)
    fun contextFilter(
        properties: ContextFilterProperties,
        servletFilterProperties: ServletFilterProperties,
        threadContext: ThreadContext,
        mappingHelper: MappingHelper,
        headerHelper: HeaderHelper,
    ): FilterRegistrationBean<ContextFilter> {
        val registrationBean = FilterRegistrationBean<ContextFilter>()
        registrationBean.filter =
            ContextFilter(threadContext, mappingHelper, headerHelper.contextHeaders, headerHelper.jsonHeaders, properties.headers)
        registrationBean.order = servletFilterProperties.baseOrder + properties.orderDelta
        return registrationBean
    }
}

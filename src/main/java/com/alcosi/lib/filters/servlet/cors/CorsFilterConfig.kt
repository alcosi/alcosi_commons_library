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

package com.alcosi.lib.filters.servlet.cors

import com.alcosi.lib.filters.servlet.FilterConfig
import com.alcosi.lib.filters.servlet.ServletFilterProperties
import org.springframework.boot.autoconfigure.AutoConfigureAfter
import org.springframework.boot.autoconfigure.condition.ConditionalOnBean
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty
import org.springframework.boot.context.properties.EnableConfigurationProperties
import org.springframework.boot.web.servlet.FilterRegistrationBean
import org.springframework.context.annotation.Bean

/**
 * The CorsFilterConfig class is responsible for configuring and registering a CorsFilter bean
 * for handling Cross-Origin Resource Sharing (CORS) requests.
 *
 * It is open for extension and is marked with annotations such as @AutoConfigureAfter,
 * @ConditionalOnBean, and @ConditionalOnProperty to ensure proper configuration and bean creation.
 *
 * The class is also annotated with @EnableConfigurationProperties to enable the use of CorsFilterProperties
 * for configuring the CorsFilter bean.
 */
@AutoConfigureAfter(FilterConfig::class)
@ConditionalOnBean(FilterConfig::class)
@ConditionalOnProperty(
    prefix = "common-lib.filter.cors",
    name = ["disabled"],
    matchIfMissing = true,
    havingValue = "false",
)
@EnableConfigurationProperties(CorsFilterProperties::class)
open class CorsFilterConfig {
    /**
     * Configures and registers a CorsFilter bean for handling Cross-Origin Resource Sharing (CORS) requests.
     *
     * @param servletFilterProperties The properties for configuring a ServletFilter.
     * @param corsFilterProperties The properties for configuring the CorsFilter.
     * @return The FilterRegistrationBean for the CorsFilter.
     */
    @Bean(name = ["corsFilterBean"], value = ["corsFilterBean"])
    fun corsFilter(
        servletFilterProperties: ServletFilterProperties,
        corsFilterProperties: CorsFilterProperties,
    ): FilterRegistrationBean<CorsFilter> {
        val registrationBean = FilterRegistrationBean<CorsFilter>()
        registrationBean.filter = CorsFilter()
        registrationBean.order = servletFilterProperties.baseOrder + corsFilterProperties.orderDelta
        return registrationBean
    }
}

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

package com.alcosi.lib.filters.servlet.cache

import com.alcosi.lib.filters.servlet.FilterConfig
import com.alcosi.lib.filters.servlet.ServletFilterProperties
import org.springframework.boot.autoconfigure.AutoConfigureAfter
import org.springframework.boot.autoconfigure.condition.ConditionalOnBean
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty
import org.springframework.boot.context.properties.EnableConfigurationProperties
import org.springframework.boot.web.servlet.FilterRegistrationBean
import org.springframework.context.annotation.Bean
import org.springframework.scheduling.annotation.Scheduled

@AutoConfigureAfter(FilterConfig::class)
@ConditionalOnBean(FilterConfig::class)
@ConditionalOnProperty(
    prefix = "common-lib.filter.cache",
    name = ["disabled"],
    matchIfMissing = true,
    havingValue = "false",
)
@EnableConfigurationProperties(CacheFilterProperties::class)
open class CacheFilterConfig {
    @ConditionalOnClass(Scheduled::class)
    @Bean(name = ["cachingRqRsFilterBean"], value = ["cachingRqRsFilterBean"])
    fun cachingFilter(
        servletFilterProperties: ServletFilterProperties,
        cacheFilterProperties: CacheFilterProperties,
    ): FilterRegistrationBean<CachingRqRsFilter> {
        val registrationBean = FilterRegistrationBean<CachingRqRsFilter>()
        registrationBean.filter = CachingRqRsFilter(cacheFilterProperties.refreshUri, cacheFilterProperties.maxBodySize, cacheFilterProperties.clearDelay)
        registrationBean.order = servletFilterProperties.baseOrder + cacheFilterProperties.orderDelta
        return registrationBean
    }
}

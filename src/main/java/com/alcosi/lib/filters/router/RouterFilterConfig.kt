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

package com.alcosi.lib.filters.router

import org.springframework.boot.autoconfigure.AutoConfiguration
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty
import org.springframework.boot.context.properties.EnableConfigurationProperties
import org.springframework.context.annotation.Bean
import org.springframework.web.servlet.function.RouterFunction

/**
 * Configuration class for Router filters.
 */
@ConditionalOnClass(RouterFunction::class)
@ConditionalOnProperty(
    prefix = "common-lib.router-filter.all",
    name = ["disabled"],
    matchIfMissing = true,
    havingValue = "false",
)
@EnableConfigurationProperties(RouterFilterCaughtExceptionsProperties::class, RouterFilterProperties::class)
@AutoConfiguration
class RouterFilterConfig {
    /**
     * Retrieves the CaughtExceptionRouterFilter instance based on the provided RouterFilterCaughtExceptionsProperties.
     *
     * @param props The properties for configuring the CaughtExceptionRouterFilter.
     * @return The instantiated CaughtExceptionRouterFilter.
     */
    @Bean
    @ConditionalOnProperty(
        prefix = "common-lib.router-filter.caught-exception",
        name = ["disabled"],
        matchIfMissing = true,
        havingValue = "false",
    )
    @ConditionalOnMissingBean(CaughtExceptionRouterFilter::class)
    fun getCaughtExceptionRouterFilter(props: RouterFilterCaughtExceptionsProperties): CaughtExceptionRouterFilter = CaughtExceptionRouterFilter(props.messageConversionErrorCode, props.unknownErrorCode)

    /**
     * Retrieves a FilteredRouterBeanPostProcessor instance based on the provided list of RouterFilters.
     *
     * @param filters The list of RouterFilters used to filter RouterFunctions.
     * @return A FilteredRouterBeanPostProcessor instance.
     */
    @Bean
    @ConditionalOnMissingBean(FilteredRouterBeanPostProcessor::class)
    fun getFilteredRouterBeanPostProcessor(filters: List<RouterFilter>): FilteredRouterBeanPostProcessor = FilteredRouterBeanPostProcessor(filters.sortedBy { it.order })
}

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

package com.alcosi.lib.filters.servlet

import org.springframework.beans.factory.annotation.Value
import org.springframework.boot.autoconfigure.AutoConfiguration
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty
import org.springframework.boot.context.properties.EnableConfigurationProperties
import org.springframework.context.annotation.Bean
import org.springframework.web.filter.OncePerRequestFilter

/**
 * The FilterConfig class is responsible for configuring and creating beans related to filters.
 * It is annotated with @AutoConfiguration and @ConditionalOnClass, ensuring that it is automatically configured when
 * the OncePerRequestFilter class is present in the classpath.
 *
 * The class is also annotated with @ConditionalOnProperty, which checks whether the property "common-lib.filter.all.disabled" is set to false (default is true
 */
@AutoConfiguration
@ConditionalOnClass(OncePerRequestFilter::class)
@ConditionalOnProperty(
    prefix = "common-lib.filter.all",
    name = ["disabled"],
    matchIfMissing = true,
    havingValue = "false",
)
@EnableConfigurationProperties(EnvironmentProperties::class, ServletFilterProperties::class)
class FilterConfig {
    /**
     * Retrieves the ThreadContext instance.
     *
     * This method is annotated with @Bean and @ConditionalOnMissingBean, ensuring that it is automatically
     * configured when there is no other bean of type ThreadContext present in the application context.
     *
     * The method creates a new ThreadContext instance and returns it to the caller.
     *
     * @return The ThreadContext instance.
     */
    @Bean
    @ConditionalOnMissingBean(ThreadContext::class)
    fun getThreadContext(): ThreadContext {
        return ThreadContext()
    }

    /**
     * Retrieves an instance of `HeaderHelper` with the given parameters.
     *
     * @param serviceName The name of the service.
     * @param environmentName The environment of the service.
     * @param environment The instance of `EnvironmentProperties`.
     * @param context The instance of `ThreadContext`.
     * @return The instance of `HeaderHelper`.
     */
    @Bean
    @ConditionalOnMissingBean(HeaderHelper::class)
    fun getHeaderHelper(
        @Value("\${spring.application.name}") serviceName: String,
        @Value("\${spring.application.environment}") environmentName: String,
        environment: EnvironmentProperties, // sometimes it's not working (values from properties file/env are not setted to EnvironmentProperties), no idea why
        context: ThreadContext,
    ): HeaderHelper {
        return HeaderHelper(serviceName, environmentName, context)
    }
}

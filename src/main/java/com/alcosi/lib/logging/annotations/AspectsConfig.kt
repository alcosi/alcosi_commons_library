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

package com.alcosi.lib.logging.annotations

import org.aspectj.lang.annotation.Aspect
import org.springframework.boot.autoconfigure.AutoConfiguration
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty
import org.springframework.boot.context.properties.EnableConfigurationProperties
import org.springframework.context.annotation.Bean

/**
 * Configuration class for Aspects*/
@ConditionalOnClass(Aspect::class)
@ConditionalOnProperty(prefix = "common-lib.aspect", name = ["disabled"], matchIfMissing = true, havingValue = "false")
@AutoConfiguration
@EnableConfigurationProperties(AspectProperties::class)
class AspectsConfig {
    /**
     * Retrieve the logging error aspect instance.
     *
     * @return An instance of LoggingErrorAspect.
     */
    @Bean
    fun getLoggingErrorAspect(): LoggingErrorAspect {
        return LoggingErrorAspect()
    }

    /**
     * Retrieves the logging time aspect.
     *
     * @return An instance of LoggingTimeAspect.
     */
    @Bean
    fun getLoggingTimeAspect(): LoggingTimeAspect {
        return LoggingTimeAspect()
    }
}

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

package com.alcosi.lib.utils

import org.springframework.boot.autoconfigure.AutoConfiguration
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean
import org.springframework.context.annotation.Bean
/**
 * Provides the configuration for the PrepareHexService.
 * This class is responsible for creating a bean instance of the PrepareHexService
 * if there is no existing bean for the same type.
 */
@AutoConfiguration
@ConditionalOnMissingBean(PrepareHexService::class)
class PrepareHexServiceConfig {
    /**
     * Gets the PrepareHexService instance.
     *
     * If a custom implementation of PrepareHexService is already registered as a bean,
     * returns the existing instance. Otherwise, creates a new instance of PrepareHexService.
     *
     * @return The PrepareHexService instance.
     */
    @Bean
    @ConditionalOnMissingBean(PrepareHexService::class)
    fun getPrepareHexService(): PrepareHexService {
        return PrepareHexService()
    }
}

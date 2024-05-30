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

package com.alcosi.lib.serializers

import com.alcosi.lib.utils.PrepareHexService
import com.alcosi.lib.utils.PrepareHexServiceConfig
import com.fasterxml.jackson.databind.ser.std.StdSerializer
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.autoconfigure.AutoConfiguration
import org.springframework.boot.autoconfigure.AutoConfigureAfter
import org.springframework.boot.autoconfigure.condition.ConditionalOnBean
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass

@ConditionalOnClass(StdSerializer::class)
@AutoConfiguration
@AutoConfigureAfter(PrepareHexServiceConfig::class)
class SerializersConfig {
    /**
     * Configures the HexStringDeSerializer by setting the PrepareHexService.
     *
     * @param prepareArgsService The PrepareHexService instance to be*/
    @Autowired
    @ConditionalOnBean(PrepareHexService::class)
    fun configureHexDeSerializer(prepareArgsService: PrepareHexService) {
        HexStringDeSerializer.setPrepareArgsService(prepareArgsService)
    }

    /**
     * Configures the HexStringSerializer by setting the PrepareHexService.
     *
     * @param prepareArgsService The PrepareHexService instance to be set.
     */
    @Autowired
    @ConditionalOnBean(PrepareHexService::class)
    fun configureHexSerializer(prepareArgsService: PrepareHexService) {
        HexStringSerializer.setPrepareArgsService(prepareArgsService)
    }

    /**
     * Configures the HexBigIntDeSerializer by setting the PrepareHexService.
     *
     * @param prepareArgsService The PrepareHexService instance to be set. Cannot be null.
     */
    @Autowired
    @ConditionalOnBean(PrepareHexService::class)
    fun configureHexBigIntDeSerializer(prepareArgsService: PrepareHexService) {
        HexBigIntDeSerializer.setPrepareArgsService(prepareArgsService)
    }
}

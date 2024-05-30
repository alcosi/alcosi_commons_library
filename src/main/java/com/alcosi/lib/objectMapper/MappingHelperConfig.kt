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

package com.alcosi.lib.objectMapper

import com.fasterxml.jackson.databind.ObjectMapper
import org.springframework.boot.autoconfigure.AutoConfiguration
import org.springframework.boot.autoconfigure.AutoConfigureAfter
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty
import org.springframework.boot.context.properties.EnableConfigurationProperties
import org.springframework.context.annotation.Bean

/**
 * This class provides configuration for MappingHelper, a helper class for mapping JSON strings or JsonNodes
 * to Java objects using the ObjectMapper class.
 *
 * The configuration is conditional based on the presence of the MappingHelper class and certain property values.
 * If the "common-lib.mapping-helper.disabled" property is missing or set to "false", the configuration will be enabled.
 * Otherwise, it will be disabled.
 *
 * The class is annotated with @Deprecated and @ConditionalOnProperty, indicating that it is deprecated and the usage
 * is discouraged. The suggested replacement is to use the extension methods provided in ObjectMapperExtension.kt.
 *
 * The class is also annotated with @AutoConfiguration and @EnableConfigurationProperties, indicating that it enables
 * the use of configuration properties defined in the MappingHelperProperties class.
 *
 * The class is configured to be auto-configured after the ObjectMapperConfig class.
 *
 * @see MappingHelper
 * @see ObjectMapper
 * @see Deprecated
 * @see ConditionalOnProperty
 * @see AutoConfiguration
 * @see EnableConfigurationProperties
 * @see ObjectMapperConfig
 */
@Deprecated("Use extension for object mapper", replaceWith = ReplaceWith("Use extension for object mapper", "com.alcosi.lib.objectMapper.ObjectMapperExtension.kt"), level = DeprecationLevel.WARNING)
@ConditionalOnProperty(prefix = "common-lib.mapping-helper", name = ["disabled"], matchIfMissing = true, havingValue = "false")
@AutoConfiguration
@EnableConfigurationProperties(MappingHelperProperties::class)
@AutoConfigureAfter(ObjectMapperConfig::class)
class MappingHelperConfig {
    /**
     * Retrieves an instance of MappingHelper for mapping JSON strings or JsonNodes to Java objects using the provided ObjectMapper.
     *
     * @param objectMapper The ObjectMapper instance to be used for mapping.
     * @return A MappingHelper instance.
     * @deprecated Use extension for object mapper. Replace with extension method provided in ObjectMapperExtension.kt.
     * @replace Use extension for object mapper. Replace with extension method provided in ObjectMapperExtension.kt.
     */
    @Deprecated("Use extension for object mapper", replaceWith = ReplaceWith("Use extension for object mapper", "com.alcosi.lib.objectMapper.ObjectMapperExtension.kt"), level = DeprecationLevel.WARNING)
    @Bean
    fun getDBMappingHelper(objectMapper: ObjectMapper): MappingHelper {
        return MappingHelper(objectMapper)
    }
}

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

import com.fasterxml.jackson.core.JsonFactory
import com.fasterxml.jackson.core.JsonFactoryBuilder
import com.fasterxml.jackson.core.StreamReadConstraints
import com.fasterxml.jackson.databind.Module
import com.fasterxml.jackson.databind.ObjectMapper
import com.fasterxml.jackson.databind.json.JsonMapper
import org.springframework.boot.autoconfigure.AutoConfiguration
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty
import org.springframework.boot.context.properties.EnableConfigurationProperties
import org.springframework.context.annotation.Bean

@ConditionalOnClass(ObjectMapper::class)
@ConditionalOnProperty(prefix = "common-lib.object-mapper", name = ["disabled"], matchIfMissing = true, havingValue = "false")
@AutoConfiguration
@EnableConfigurationProperties(ObjectMapperProperties::class)
class ObjectMapperConfig {
    @Bean
    @ConditionalOnMissingBean(ObjectMapper::class)
    fun getObjectMapper(
        props: ObjectMapperProperties,
        factory: JsonFactory,
        modules: List<Module>,
    ): ObjectMapper {
        val builder = JsonMapper.builder(factory)
        props.mappingFuture.forEach {
                prop ->
            builder.configure(prop.key, prop.value)
        }
        props.serializationFeature.forEach {
                prop ->
            builder.configure(prop.key, prop.value)
        }
        props.deserializationFeature.forEach {
                prop ->
            builder.configure(prop.key, prop.value)
        }
        props.parserFeature.forEach {
                prop ->
            builder.configure(prop.key, prop.value)
        }
        val mapper =
            builder
                .build()
        mapper.registerModules(modules)
        mapper.findAndRegisterModules()
        return mapper
    }

    @Bean
    @ConditionalOnMissingBean(JsonFactory::class)
    fun getJsonFactory(props: ObjectMapperProperties): JsonFactory {
        val stringSettings =
            StreamReadConstraints
                .builder()
                .maxStringLength(props.string.maxStringLength)
                .maxNestingDepth(props.string.maxNestingDepth)
                .maxNumberLength(props.string.maxNumberLength)
                .build()
        val factory =
            JsonFactoryBuilder()
                .streamReadConstraints(stringSettings)
                .build()
        return factory
    }
}

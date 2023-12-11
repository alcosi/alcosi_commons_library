/*
 * Copyright (c) 2024  Alcosi Group Ltd. and affiliates.
 *
 * Portions of this software are licensed as follows:
 *
 *     All content that resides under the "alcosi" and "atomicon" or “deploy” directories of this repository, if that directory exists, is licensed under the license defined in "LICENSE.TXT".
 *
 *     All third-party components incorporated into this software are licensed under the original license provided by the owner of the applicable component.
 *
 *     Content outside of the above-mentioned directories or restrictions above is available under the MIT license as defined below.
 *
 *
 *
 * MIT License
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy of this software and associated documentation files (the "Software"), to deal in the Software without restriction, including without limitation the rights to use, copy, modify, merge, publish, distribute, sublicense, and/or sell copies of the Software, and to permit persons to whom the Software is urnished to do so, subject to the following conditions:
 *
 *
 *
 * The above copyright notice and this permission notice shall be included in all copies or substantial portions of the Software.
 *
 *
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY, FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM, OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE SOFTWARE.
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

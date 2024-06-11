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

import com.fasterxml.jackson.databind.JsonMappingException
import org.junit.jupiter.api.Assertions.assertNotNull
import org.junit.jupiter.api.Assertions.assertThrows
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.ExtendWith
import org.springframework.test.context.junit.jupiter.SpringExtension
import java.util.logging.Level
import java.util.logging.Logger

@ExtendWith(SpringExtension::class)
class ObjectMapperTest {
    data class SerializationTest(val key: String)

    @Test
    fun testLargeStringMappingConfigured() {
        try {
            val stringSettings = ObjectMapperProperties.StringSettings()
            stringSettings.maxStringLength = Int.MAX_VALUE
            val properties = ObjectMapperProperties()
            properties.string = stringSettings
            val objectMapperConfig = ObjectMapperConfig()
            val factory = objectMapperConfig.getJsonFactory(properties)
            val mapper = objectMapperConfig.getObjectMapper(properties, factory, listOf())
            val time = System.currentTimeMillis()
            val deserialized = mapper.readValue(getBigJson(100), SerializationTest::class.java)
            val took = System.currentTimeMillis() - time
            logger.log(Level.INFO, "Took ${took}ms")
            assertNotNull(deserialized)
        } catch (t: Throwable) {
            logger.log(Level.SEVERE, "Error! ", t)
            throw t
        }
    }

    @Test
    fun testLargeStringMappingConfiguredRepeated() {
        try {
            (0..10).forEach {
                testLargeStringMappingConfigured()
            }
        } catch (t: Throwable) {
            logger.log(Level.SEVERE, "Error! ", t)
            throw t
        }
    }

    private fun getBigString(mb: Int): String {
        return "1"
            .repeat(1000)
            .repeat(1000)
            .repeat(mb)
    }

    @Test
    fun testLargeStringMappingDefault() {
        assertThrows(JsonMappingException::class.java) {
            val stringSettings = ObjectMapperProperties.StringSettings()
            val properties = ObjectMapperProperties()
            properties.string = stringSettings
            val objectMapperConfig = ObjectMapperConfig()
            val factory = objectMapperConfig.getJsonFactory(properties)
            val mapper = objectMapperConfig.getObjectMapper(properties, factory, listOf())
            val deserialized = mapper.readValue(getBigJson(100), SerializationTest::class.java)
        }
    }

    private fun getBigJson(mb: Int): String {
        return "{\"key\":\"${getBigString(mb)}\"}"
    }

    companion object {
        val logger = Logger.getLogger(this.javaClass.name)
    }
}

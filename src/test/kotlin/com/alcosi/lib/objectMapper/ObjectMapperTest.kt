/*
 * Copyright (c) 2023  Alcosi Group Ltd. and affiliates.
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

import com.fasterxml.jackson.databind.JsonMappingException
import com.fasterxml.jackson.module.kotlin.readValue
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
            val deserialized = mapper.readValue<SerializationTest>(getBigJson(100))
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
            val deserialized = mapper.readValue<SerializationTest>(getBigJson(100))
        }
    }

    private fun getBigJson(mb: Int): String {
        return "{\"key\":\"${getBigString(mb)}\"}"
    }

    companion object {
        val logger = Logger.getLogger(this.javaClass.name)
    }
}

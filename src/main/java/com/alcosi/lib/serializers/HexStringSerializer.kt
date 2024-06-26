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
import com.fasterxml.jackson.core.JsonGenerator
import com.fasterxml.jackson.databind.SerializerProvider
import com.fasterxml.jackson.databind.ser.std.StdSerializer

/**
 * HexStringSerializer is a custom serializer for serializing Strings as hexadecimal values.
 *
 * @constructor Creates a HexStringSerializer.
 */
open class HexStringSerializer : StdSerializer<String>(
    String::class.java,
) {
    /**
     * Serializes a String value to JSON using the provided JsonGenerator and SerializerProvider.
     *
     * @param value The String value to be serialized.
     * @param gen The JsonGenerator object to write the JSON output.
     * @param provider The SerializerProvider object for accessing serializers.
     * @throws IllegalStateException If the class of the value is not String.
     */
    override fun serialize(
        value: String?,
        gen: JsonGenerator,
        provider: SerializerProvider,
    ) {
        if (value == null) {
            gen.writeNull()
        } else {
            if (value is String) {
                writeString(gen, value)
            } else {
                throw IllegalStateException("Wrong class " + value.javaClass)
            }
        }
    }

    /**
     * Writes a String value into a JsonGenerator after preprocessing it with the getAddr() method.
     *
     * @param gen The JsonGenerator object to write the JSON output.
     * @param a The String value to be written.
     */
    protected open fun writeString(
        gen: JsonGenerator,
        a: Any,
    ) {
        gen.writeString(getAddr(a as String))
    }

    /**
     * Returns the formatted address based on the provided value.
     *
     * @param value the address value to be formatted
     * @return the formatted address
     */
    protected open fun getAddr(value: String): String {
        return if (value.startsWith("0x") || value.startsWith("0X")) {
            value
        } else {
            "0x" + prepareArgsService!!.prepareHexNoMatcher(value)
        }
    }

    companion object {
        /**
         * Sets the PrepareHexService instance for the HexStringSerializer class.
         *
         * @param prepareArgsService The PrepareHexService instance to be set.
         */
        fun setPrepareArgsService(prepareArgsService: PrepareHexService?) {
            Companion.prepareArgsService = prepareArgsService
        }

        /**
         * Service for preparing arguments in hexadecimal format.
         * This service object is responsible for preparing arguments to be in hexadecimal format.
         */
        private var prepareArgsService: PrepareHexService? = null
    }
}

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

class HexStringSerializer : StdSerializer<String>(
    String::class.java,
) {
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

    private fun writeString(
        gen: JsonGenerator,
        a: Any,
    ) {
        gen.writeString(getAddr(a as String))
    }

    private fun getAddr(value: String): String {
        return if (value.startsWith("0x") || value.startsWith("0X")) {
            value
        } else {
            "0x" + prepareArgsService!!.prepareHexNoMatcher(value)
        }
    }

    companion object {
        fun setPrepareArgsService(prepareArgsService: PrepareHexService?) {
            Companion.prepareArgsService = prepareArgsService
        }

        private var prepareArgsService: PrepareHexService? = null
    }
}

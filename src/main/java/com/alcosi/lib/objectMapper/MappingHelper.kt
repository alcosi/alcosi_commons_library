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

import com.fasterxml.jackson.core.type.TypeReference
import com.fasterxml.jackson.databind.JavaType
import com.fasterxml.jackson.databind.JsonNode
import com.fasterxml.jackson.databind.ObjectMapper
import java.util.*

class MappingHelper(protected val objectMapper: ObjectMapper) {
    fun <T : Any> mapOne(
        s: String?,
        c: Class<T>,
    ): T? {
        return if (s == null) {
            null
        } else {
            (objectMapper.readValue(s, c))
        }
    }

    fun <T : Any> mapOneNode(
        s: JsonNode?,
        c: Class<T>,
    ): T? {
        return if (s == null) {
            null
        } else {
            objectMapper.treeToValue(s, c)
        }
    }

    fun <T : Any> mapOne(
        s: String?,
        c: TypeReference<T>,
    ): T? {
        return if (s == null) {
            null
        } else {
            (objectMapper.readValue(s, c))
        }
    }

    fun <T : Any> mapOneNode(
        s: JsonNode?,
        c: JavaType,
    ): T? {
        return if (s == null) {
            null
        } else {
            objectMapper.treeToValue(s, c)
        }
    }

    fun serialize(o: Any?): String? {
        return if (o == null) {
            null
        } else {
            objectMapper.writeValueAsString(o).replace("\u0000", "<0x00>")
        }
    }

    fun <T : Any> mapList(
        s: String?,
        c: Class<T>,
    ): List<T> {
        if (s == null) {
            return emptyList()
        }
        val arrayType = c.arrayType()
        val t = objectMapper.readValue(s, arrayType) as Array<*>
        return Arrays
            .stream(t)
            .map { t -> t as T }
            .toList()
    }

    fun <T : Any> mapListNode(
        s: JsonNode?,
        c: Class<T>,
    ): List<T> {
        return if (s == null) {
            return emptyList()
        } else {
            val arrayType = c.arrayType()
            val t = objectMapper.treeToValue(s, arrayType) as Array<*>
            return mapArrayToList(t)
        }
    }

    private fun <T : Any> mapArrayToList(t: Array<*>): MutableList<T> =
        Arrays
            .stream(t)
            .map { it as T }
            .toList()
}

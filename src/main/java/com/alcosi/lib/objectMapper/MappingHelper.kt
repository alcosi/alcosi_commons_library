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

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

/**
 * @deprecated Use extension for object mapper
 * @replace
 */
@Deprecated("Use extension for object mapper", replaceWith = ReplaceWith("Use extension for object mapper", "com.alcosi.lib.objectMapper.ObjectMapperExtension.kt"), level = DeprecationLevel.WARNING)
open class MappingHelper(val objectMapper: ObjectMapper) {
    /**
     * Maps the given JSON string to an instance of the specified class.
     *
     * @param s The JSON string to map. Can be null.
     * @param c The class to map to.
     * @return The mapped instance of the class, or null if the JSON string is
     *     null.
     */
    open fun <T : Any> mapOne(
        s: String?,
        c: Class<T>,
    ): T? {
        return objectMapper.mapOne(s, c)
    }

    /**
     * Maps a single JSON node to an object of the specified class.
     *
     * @param s The JSON node to map.
     * @param c The class to map the JSON node to.
     * @return An object of the specified class if the JSON node is not null, otherwise null.
     */
    open fun <T : Any> mapOneNode(
        s: JsonNode?,
        c: Class<T>,
    ): T? {
        return objectMapper.mapOneNode(s, c)
    }

    /**
     * Maps a JSON string to an object of the specified type.
     *
     * @param s The JSON string to be mapped. Can be null.
     * @param c The type reference of the object to be mapped to.
     * @return The mapped object of type T, or null if the input string is null.
     */
    open fun <T : Any> mapOne(
        s: String?,
        c: TypeReference<T>,
    ): T? {
        return objectMapper.mapOne(s, c)
    }

    /**
     * Maps the given [JsonNode] to an instance of the specified class [T].
     *
     * @param s The [JsonNode] to map. Can be null.
     * @param c The class to map to.
     * @return The mapped
     */
    open fun <T : Any> mapOneNode(
        s: JsonNode?,
        c: JavaType,
    ): T? {
        return objectMapper.mapOneNode(s, c)
    }

    /**
     */
    open fun serialize(o: Any?): String? {
        return objectMapper.serialize(o)
    }

    /**
     * Maps the given JSON string to a list of instances of the specified class.
     *
     * @param s The JSON string to map. Can be null.
     * @param c The class to map to.
     * @return The mapped list of*/
    open fun <T : Any> mapList(
        s: String?,
        c: Class<T>,
    ): List<T> {
        return objectMapper.mapList(s, c)
    }

    /**
     * Maps the given [JsonNode] to a list of instances of the specified class [T].
     *
     * @param s The [JsonNode] to map. Can be null.
     * @param c The class to map to.
     * @return The mapped list of instances of the class, or an empty list if the [JsonNode] is null.
     */
    open fun <T : Any> mapListNode(
        s: JsonNode?,
        c: Class<T>,
    ): List<T> {
        return objectMapper.mapListNode(s, c)

    }

}

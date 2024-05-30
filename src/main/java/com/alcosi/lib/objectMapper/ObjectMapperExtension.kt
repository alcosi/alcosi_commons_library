/*
 *
 *  * Copyright (c) 2024 Alcosi Group Ltd. and affiliates.
 *  *
 *  * Licensed under the Apache License, Version 2.0 (the "License");
 *  * you may not use this file except in compliance with the License.
 *  * You may obtain a copy of the License at
 *  *
 *  *     http://www.apache.org/licenses/LICENSE-2.0
 *  *
 *  * Unless required by applicable law or agreed to in writing, software
 *  * distributed under the License is distributed on an "AS IS" BASIS,
 *  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *  * See the License for the specific language governing permissions and
 *  * limitations under the License.
 *  *
 *
 */

package com.alcosi.lib.objectMapper

import com.fasterxml.jackson.core.type.TypeReference
import com.fasterxml.jackson.databind.JavaType
import com.fasterxml.jackson.databind.JsonNode
import com.fasterxml.jackson.databind.ObjectMapper
/**
 * Converts a JSON string into an instance of the specified class using the provided ObjectMapper.
 *
 * @param s The JSON string to be converted.
 * @param c The class of the object to be mapped.
 * @return An instance of the specified class representing the JSON string. Returns null if the input JSON string is null.
 */
fun <T : Any> ObjectMapper.mapOne(
    s: String?,
    c: Class<T>,
): T? {
    return if (s == null) {
        null
    } else {
        this.readValue(s, c)
    }
}
/**
 * Maps a JSON string to an object of the specified type.
 *
 * @param s The JSON string to be mapped.
 * @param c The type reference of the object to be mapped to.
 * @return The mapped object of type T. If the input string is null, returns null.
 */
fun <T : Any> ObjectMapper.mapOne(
    s: String?,
    c: TypeReference<T>,
): T? {
    return if (s == null) {
        null
    } else {
        this.readValue(s, c)
    }
}
/**
 * Maps the given JSON string to an instance of the specified class using the Jackson ObjectMapper.
 *
 * @param s The JSON string to be mapped.
 * @param c The JavaType representing the class to which the JSON string should be mapped.
 * @return An instance of the specified class if the JSON string is not null, otherwise null.
 */
fun <T : Any> ObjectMapper.mapOne(
    s: String?,
    c: JavaType,
): T? {
    return if (s == null) {
        null
    } else {
        this.readValue(s, c)
    }
}
/**
 * Maps a JSON string to an object of type T using the ObjectMapper.
 *
 * @param s the JSON string to be mapped
 * @return the mapped object of type T, or null if the JSON string is null
 */
inline fun <reified T : Any> ObjectMapper.mapOne(s: String?): T? {
    return this.mapOne(s, T::class.java)
}
/**
 * Maps a single JSON node to an object of the specified class.
 *
 * @param s The JSON node to map.
 * @param c The class to map the JSON node to.
 * @return An object of the specified class if the JSON node is not null, otherwise null.
 */
fun <T : Any> ObjectMapper.mapOneNode(
    s: JsonNode?,
    c: Class<T>,
): T? {
    return if (s == null) {
        null
    } else {
        this.treeToValue(s, c)
    }
}
/**
 * Maps a single JSON node to the specified type using the ObjectMapper.
 *
 * @param s The JSON node to map.
 * @param c The type reference of the desired object.
 * @return The mapped object or null if the JSON node is null.
 */
fun <T : Any> ObjectMapper.mapOneNode(
    s: JsonNode?,
    c: TypeReference<T>
): T? {
    return if (s == null) {
        null
    } else {
        this.treeToValue(s, c)
    }
}

/**
 * Maps a single JSON node to the specified type using the ObjectMapper.
 *
 * @param s the JSON node to map
 * @param c the JavaType representing the target type
 * @return the mapped object of type T, or null if the provided JSON node is null
 */
fun <T : Any> ObjectMapper.mapOneNode(
    s: JsonNode?,
    c: JavaType,
): T? {
    return if (s == null) {
        null
    } else {
        this.treeToValue(s, c)
    }
}
/**
 * Maps a single JSON node to the specified type using the configured `ObjectMapper`.
 *
 * @param T the target type to map the JSON node to. Must be a class that is compatible with the `ObjectMapper`'s configuration.
 * @param s the JSON node to be mapped.
 * @return the mapped object of type `T` if the JSON node is not `null`, `null` otherwise.
 */
inline fun <reified T : Any> ObjectMapper.mapOneNode(s: JsonNode?): T? {
    return this.mapOneNode(s, T::class.java)
}
/**
 * Maps a JSON string to a list of objects of the specified class using the ObjectMapper.
 *
 * @param s The JSON string to be mapped.
 * @param c The class of the objects in the resulting list.
 * @param <T> The type of the objects in the resulting list, must be a non-null type.
 * @return A list of objects of the specified class mapped from the JSON string.
 */
fun <T : Any> ObjectMapper.mapList(
    s: String?,
    c: Class<T>,
): List<T> {
    if (s == null) {
        return emptyList()
    }
    val arrayType = c.arrayType()
    val array = this.readValue(s, arrayType) as Array<T>
    return array.toList()
}

/**
 * Maps a JsonNode representing an array to a list of objects of type T.
 *
 * @param s The JsonNode to be mapped.
 * @param c The Class of the objects in the resulting list.
 * @return A list of objects of type T.
 */
fun <T : Any> ObjectMapper.mapListNode(
    s: JsonNode?,
    c: Class<T>,
): List<T> {
    return if (s == null) {
        emptyList()
    } else {
        val arrayType = c.arrayType()
        val array = this.treeToValue(s, arrayType) as Array<T>
        array.toList()
    }
}
/**
 * Maps the given JSON string to a list of objects of type T using the ObjectMapper instance.
 *
 * @param T the type of the objects in the list
 * @param s the JSON string to be mapped
 * @return the list of objects of type T obtained from parsing the JSON string
 */
inline fun <reified T : Any> ObjectMapper.mapList(
    s: String?,
): List<T> {
    return this.mapList(s, T::class.java)
}
/**
 * Maps a JsonNode to a List of objects of type T using the ObjectMapper.
 *
 * @param s The JsonNode to be mapped.
 * @return The List of objects of type T mapped from the JsonNode. If the JsonNode is null, an empty list is returned.
 */
inline fun <reified T : Any> ObjectMapper.mapListNode(
    s: JsonNode?,
): List<T> {
    return this.mapListNode(s, T::class.java)
}


/**
 * List of replacement pairs for serialization.
 *
 * Each pair consists of a character to be replaced and its corresponding replacement string.
 * This list is used during serialization to replace specific characters with their representation.
 * Serialization is done by the `serialize` function of the `ObjectMapper` class.
 * The replacement is performed using the `replace` method of `String` class.
 *
 * Example:
 * - Original string: "Some\u0000Text"
 * - Replace pair: `"\u0000"` to `"<0x00>"`
 * - Serialized string after replacement: "Some<0x00>Text"
 *
 * @see ObjectMapper.serialize
 */
val serializationReplacePairs = listOf("\u0000" to "<0x00>")
/**
 * Serializes the given object to a JSON string using the Jackson ObjectMapper.
 *
 * @param o the object to be serialized
 * @return the JSON string representation of the object, or null if the object is null
 */
fun ObjectMapper.serialize(o: Any?): String? {
    return if (o == null) {
        null
    } else {
        serializationReplacePairs.fold(this.writeValueAsString(o)) { acc, pair -> acc.replace(pair.first, pair.second) }
    }
}
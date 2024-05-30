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

import com.fasterxml.jackson.databind.JsonNode

/**
 * Maps the given JSON string to an instance of the specified class.
 *
 * @param s The JSON string to map. Can be null.
 * @param c The class to map to.
 * @return The mapped instance of the class, or null if the JSON string is
 *     null.
 */
@Deprecated("Use extension for object mapper", replaceWith = ReplaceWith("Use extension for object mapper", "com.alcosi.lib.objectMapper.ObjectMapperExtension.kt"), level = DeprecationLevel.WARNING)
inline fun <reified T : Any> MappingHelper.mapOne(s: String?): T? {
    return mapOne(s, T::class.java)
}

/**
 * Maps a single JSON node to an object of type T using the MappingHelper.
 *
 * @param s The JSON node to map.
 * @return The mapped object of type T, or null if the mapping fails.
 * @throws IllegalArgumentException if T is not a non-null class.
 */
@Deprecated("Use extension for object mapper", replaceWith = ReplaceWith("Use extension for object mapper", "com.alcosi.lib.objectMapper.ObjectMapperExtension.kt"), level = DeprecationLevel.WARNING)
inline fun <reified T : Any> MappingHelper.mapOneNode(s: JsonNode?): T? {
    return mapOneNode(s, T::class.java)
}

/**
 * Maps the given JSON string to a list of instances of the specified
 * class.
 *
 * @param s The JSON string to map. Can be null.
 * @param c The class to map to.
 * @return The mapped list of instances of the class, or an empty list if
 *     the JSON string is null.
 */
@Deprecated("Use extension for object mapper", replaceWith = ReplaceWith("Use extension for object mapper", "com.alcosi.lib.objectMapper.ObjectMapperExtension.kt"), level = DeprecationLevel.WARNING)
inline fun <reified T : Any> MappingHelper.mapList(s: String?): List<T> {
    return mapList(s, T::class.java)
}

/**
 * Maps the given [JsonNode] to a list of instances of the specified class
 * [T].
 *
 * @param s The [JsonNode] to map. Can be null.
 * @param c The class to map to.
 * @return The mapped list of instances of the class, or an empty list if
 *     the [JsonNode] is null.
 */
@Deprecated("Use extension for object mapper", replaceWith = ReplaceWith("Use extension for object mapper", "com.alcosi.lib.objectMapper.ObjectMapperExtension.kt"), level = DeprecationLevel.WARNING)
inline fun <reified T : Any> MappingHelper.mapListNode(s: JsonNode?): List<T> {
    return mapListNode(s, T::class.java)
}

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

inline fun <reified T : Any> MappingHelper.mapOne(s: String?): T? {
    return mapOne(s, T::class.java)
}

inline fun <reified T : Any> MappingHelper.mapOneNode(s: JsonNode?): T? {
    return mapOneNode(s, T::class.java)
}

inline fun <reified T : Any> MappingHelper.mapList(s: String?): List<T> {
    return mapList(s, T::class.java)
}

inline fun <reified T : Any> MappingHelper.mapListNode(s: JsonNode?): List<T> {
    return mapListNode(s, T::class.java)
}

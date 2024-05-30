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

package com.alcosi.lib.filters.servlet

/**
 * Retrieves the value associated with the specified key as an enum. If no value is found, the default value is returned.
 *
 * @param key the*/
inline fun <reified T : Enum<T>> ThreadContext.getEnum(
    key: String,
    default: T,
): T {
    return getEnumOrNull<T>(key) ?: default
}

/**
 * Retrieves the enum value associated with the specified key from the ThreadContext.
 * Returns null if no value is found.
 *
 * @param key the key to retrieve the value for
 * @return the enum value associated with the key, or null if no value is found
 */
inline fun <reified T : Enum<T>> ThreadContext.getEnumOrNull(key: String): T? {
    return enumValues<T>().firstOrNull { it.name == getAll()[key] }
}

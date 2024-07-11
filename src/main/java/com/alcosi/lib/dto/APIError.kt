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

package com.alcosi.lib.dto

/**
 * Represents an API error with relevant information such as message, code, error class, and HTTP code.
 *
 * @property message The error message.
 * @property code The error code.
 * @property errorClass The error class.
 * @property httpCode The HTTP code derived from the error code.
 */

data class APIError(
    val message: String,
    val code: Int,
    val errorClass: String,
    val httpCode: Int = code.toString().take(3).toInt(),
) {
    /**
     * Returns a string representation of the [APIError] instance.
     *
     * The string representation consists of the error class, error code, and error message
     * concatenated with ":" as the separator.
     *
     * @return A string representation of the [APIError] instance.
     */
    override fun toString(): String = "$errorClass:$code:$message"
}

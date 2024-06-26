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

package com.alcosi.lib.secured.encrypt

import com.alcosi.lib.secured.container.SecuredDataContainer
import com.fasterxml.jackson.databind.ObjectMapper
import org.apache.commons.codec.binary.Hex
import java.util.regex.Pattern

/**
 * A class that represents a sensitive component for handling sensitive data.
 *
 * @param objectMapper The ObjectMapper used for JSON serialization and deserialization.
 * @param sensitiveDataPattern The regex pattern used to identify sensitive data in a string.
 */
open class SensitiveComponent(
    val objectMapper: ObjectMapper,
    protected val sensitiveDataPattern: String = "(<SensitiveData>)(([\\da-fA-F]*)|(null))(</{1,2}SensitiveData>)",
) {
    /**
     * Regular expression used to match sensitive data pattern.
     */
    open val regex = Regex(sensitiveDataPattern)

    /**
     * Regular expression pattern for matching sensitive data within curly braces.
     *
     * The pattern matches the substring within curly braces that contains sensitive data.
     *
     * @see Pattern
     */
    open val regexPattern = Pattern.compile("(\\{[^{}]*)$sensitiveDataPattern([^{}]*})")

    /**
     * Deserializes a string value to a byte array.
     *
     * @param value The string value to be deserialized.
     * @return The deserialized byte array, or null if the value is null.
     * @throws IllegalArgumentException If the encoded json value format is incorrect.
     */
    open fun deserialize(value: String?): ByteArray? {
        if (value == null) {
            return null
        }
        val matchResult =
            regex.find(value)
                ?: throw IllegalArgumentException("Wrong encoded json value format. Should start with <SensitiveData> and end with </SensitiveData>")
        val group = matchResult.groupValues[2]
        return if (group.isBlank()) {
            ByteArray(0)
        } else {
            if (group == "null") {
                null
            } else {
                Hex.decodeHex(group)
            }
        }
    }

    /**
     * Serializes a byte array to a string representation.
     *
     * @param value The byte array to be serialized.
     * @return The serialized string representation of the byte array, or null if the value is null.
     */
    open fun serialize(value: ByteArray?): String? {
        if (value == null) {
            return "<SensitiveData>null</SensitiveData>"
        }
        return "<SensitiveData>${Hex.encodeHexString(value)}</SensitiveData>"
    }

    /**
     * Decrypts a given value using the provided key.
     *
     * @param value The value to decrypt.
     * @param key The key used for decryption.
     * @return The decrypted value if successful, or null if the input value is null or an error occurred during decryption.
     */
    open fun decrypt(
        value: String?,
        key: ByteArray,
    ): String? {
        if (value == null) {
            return null
        }
        val matchResult = regexPattern.matcher(value)
        val matchResultList =
            matchResult
                .results()
                .sorted { o1, o2 -> o2.end().compareTo(o1.end()) }
                .toList()
        val replaced =
            matchResultList.fold(value) { string, m ->
                val start = m.start()
                val end = m.end()
                val jsonPart = string.substring(start, end)
                val container = objectMapper.readValue(jsonPart, SecuredDataContainer::class.java)
                val decodedValue = container.decoded(key)
                val decodedJson = objectMapper.writeValueAsString(decodedValue)
                val replaceRange = string.replaceRange(start, end, decodedJson)
                return@fold replaceRange
            }
        return replaced
    }
}

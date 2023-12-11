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

package com.alcosi.lib.secured.encrypt

import com.alcosi.lib.secured.container.SecuredDataContainer
import com.fasterxml.jackson.databind.ObjectMapper
import org.apache.commons.codec.binary.Hex
import java.util.regex.Pattern

open class SensitiveComponent(
    val objectMapper: ObjectMapper,
    protected val sensitiveDataPattern: String = "(<SensitiveData>)(([\\da-fA-F]*)|(null))(</{1,2}SensitiveData>)",
) {
    open val regex = Regex(sensitiveDataPattern)
    open val regexPattern = Pattern.compile("(\\{[^{}]*)$sensitiveDataPattern([^{}]*})")

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

    open fun serialize(value: ByteArray?): String? {
        if (value == null) {
            return "<SensitiveData>null</SensitiveData>"
        }
        return "<SensitiveData>${Hex.encodeHexString(value)}</SensitiveData>"
    }

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

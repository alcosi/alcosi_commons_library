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

package com.alcosi.lib.serializers

import com.alcosi.lib.utils.PrepareHexService
import com.fasterxml.jackson.core.JsonParser
import com.fasterxml.jackson.databind.DeserializationContext
import com.fasterxml.jackson.databind.deser.std.StdDeserializer
import com.fasterxml.jackson.databind.node.NullNode

/**
 * HexStringDeSerializer class is a custom deserializer that converts a hexadecimal string representation
 * into a String object. It extends the StdDeserializer class and overrides the deserialize method.
 * It also provides a companion object for setting the PrepareHexService dependency.
 */
open class HexStringDeSerializer : StdDeserializer<String?>(
    Any::class.java,
) {
    /**
     * Deserializes a JSON node into a String object.
     *
     * @param p The JsonParser object. Must not be null.
     * @param ctxt The DeserializationContext object. Must not be null.
     * @return The deserialized String object, or null if the JSON node is null.
     */
    override fun deserialize(
        p: JsonParser?,
        ctxt: DeserializationContext?,
    ): String? {
        if (p == null) {
            return null
        }
        val jsonNode = ctxt!!.readTree(p)
        return if (jsonNode is NullNode) {
            null
        } else {
            prepareArgsService!!.prepareHexNoMatcher(jsonNode.textValue())
        }
    }

    companion object {
        /**
         * Sets the PrepareHexService for the HexStringDeSerializer.
         *
         * @param prepareArgsService The PrepareHexService to be set. Can be null.
         */
        fun setPrepareArgsService(prepareArgsService: PrepareHexService?) {
            Companion.prepareArgsService = prepareArgsService
        }

        /**
         * Reference to the PrepareHexService instance used for preparing arguments.
         */
        private var prepareArgsService: PrepareHexService? = null
    }
}

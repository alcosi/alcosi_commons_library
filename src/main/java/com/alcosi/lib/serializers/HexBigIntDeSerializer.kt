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
import java.math.BigInteger

/**
 * HexBigIntDeSerializer is a class that provides deserialization of JSON strings into BigInteger objects.
 *
 * This class extends the StdDeserializer class and specifically handles deserialization for BigInteger objects.
 *
 * @constructor Creates an instance of HexBigIntDeSerializer
 * @param type The type of the object to be deserialized (in this case, BigInteger)
 */
class HexBigIntDeSerializer : StdDeserializer<BigInteger>(BigInteger::class.java) {
    /**
     * Deserializes a JSON string into a BigInteger object.
     *
     * @param p The JSONParser object used to parse the JSON input.
     * @param ctxt The DeserializationContext object for the deserialization process.
     * @return The deserialized BigInteger object, or null if the JSON input is null.
     */
    override fun deserialize(
        p: JsonParser?,
        ctxt: DeserializationContext?,
    ): BigInteger? {
        if (p == null) {
            return null
        }
        val jsonNode = ctxt!!.readTree(p)
        return if (jsonNode is NullNode) {
            null
        } else {
            return BigInteger(prepareArgsService!!.prepareHexNoMatcher(jsonNode.textValue()), 16)
        }
    }

    companion object {
        /**
         * Sets the PrepareHexService used by the HexBigIntDeSerializer class.
         *
         * @param prepareArgsService The PrepareHexService object to be set.
         */
        fun setPrepareArgsService(prepareArgsService: PrepareHexService?) {
            Companion.prepareArgsService = prepareArgsService
        }

        private var prepareArgsService: PrepareHexService? = null
    }
}

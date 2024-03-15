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

class HexStringDeSerializer : StdDeserializer<String?>(
    Any::class.java,
) {
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
        fun setPrepareArgsService(prepareArgsService: PrepareHexService?) {
            Companion.prepareArgsService = prepareArgsService
        }

        private var prepareArgsService: PrepareHexService? = null
    }
}

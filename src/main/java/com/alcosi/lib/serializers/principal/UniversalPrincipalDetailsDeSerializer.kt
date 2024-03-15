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

package com.alcosi.lib.serializers.principal

import com.alcosi.lib.security.DefaultPrincipalDetails
import com.fasterxml.jackson.annotation.JsonIgnoreProperties
import com.fasterxml.jackson.core.JsonParser
import com.fasterxml.jackson.databind.DeserializationContext
import com.fasterxml.jackson.databind.JsonNode
import com.fasterxml.jackson.databind.deser.std.StdDeserializer
import com.fasterxml.jackson.databind.node.NullNode

open class UniversalPrincipalDetailsDeSerializer<T : UniversalPrincipalDetailsDeSerializer.PrincipalSerializationObject>(claszz: Class<out DefaultPrincipalDetails>) : StdDeserializer<DefaultPrincipalDetails?>(claszz) {
    @JsonIgnoreProperties(ignoreUnknown = true)
    open class PrincipalSerializationObject(
        val id: String,
        val authorities: List<String>,
        val className: String,
        val type: String,
    )

    override fun deserialize(
        p: JsonParser?,
        ctxt: DeserializationContext?,
    ): DefaultPrincipalDetails? {
        if (ctxt == null || p == null) {
            return getNullValue(ctxt)
        }

        val node: JsonNode = p.codec.readTree(p)
        if (node is NullNode) {
            return getNullValue(ctxt)
        }
        val serializationObject = getInternalTypeObject(ctxt, node)
        val originalClass = Class.forName(serializationObject.className)
        if (_valueClass == originalClass) {
            return returnRealObject(serializationObject)
        } else {
            return p.codec.treeToValue(node, originalClass) as DefaultPrincipalDetails?
        }
    }

    protected open fun returnRealObject(serializationObject: T): DefaultPrincipalDetails {
        return DefaultPrincipalDetails(serializationObject.id, serializationObject.authorities, serializationObject.className, serializationObject.type)
    }

    protected open fun getInternalTypeObject(
        ctxt: DeserializationContext,
        node: JsonNode,
    ): T {
        return ctxt.readTreeAsValue(node, PrincipalSerializationObject::class.java) as T
    }
}

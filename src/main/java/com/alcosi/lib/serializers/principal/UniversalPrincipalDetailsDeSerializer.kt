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

/**
 * UniversalPrincipalDetailsDeSerializer is a class that extends StdDeserializer to provide deserialization for DefaultPrincipalDetails objects.
 *
 * @param T The type of the PrincipalSerializationObject.
 * @property claszz The class object for DefaultPrincipalDetails.
 * @constructor Creates an instance of UniversalPrincipalDetailsDeSerializer.
 */
open class UniversalPrincipalDetailsDeSerializer<T : UniversalPrincipalDetailsDeSerializer.PrincipalSerializationObject>(
    claszz: Class<out DefaultPrincipalDetails>,
) : StdDeserializer<DefaultPrincipalDetails?>(claszz) {
    /**
     * Represents the serialization object for a Principal.
     *
     * @property id The ID of the Principal.
     * @property authorities The list of authorities associated with the Principal.
     * @property className The class name of the Principal.
     * @property type The type of the Principal.
     */
    @JsonIgnoreProperties(ignoreUnknown = true)
    open class PrincipalSerializationObject(
        val id: String,
        val authorities: List<String>,
        val className: String,
        val type: String,
        val additionalProperties: Map<String, String> = mapOf(),
    )

    /**
     * Deserializes a JSON object into a DefaultPrincipalDetails object.
     *
     * @param p The JSON parser.
     * @param ctxt The deserialization context.
     * @return The deserialized DefaultPrincipalDetails object, or null if either p or ctxt is null.
     */
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

    /**
     * Returns a real object of type [DefaultPrincipalDetails] based on the provided [serializationObject].
     *
     * @param serializationObject The object used for serialization.
     * @return The [DefaultPrincipalDetails] object created from [serializationObject].
     */
    protected open fun returnRealObject(serializationObject: T): DefaultPrincipalDetails =
        DefaultPrincipalDetails(serializationObject.id, serializationObject.authorities, serializationObject.className, serializationObject.type, serializationObject.additionalProperties)

    /**
     * Retrieves the internal type object from the given JsonNode using the provided DeserializationContext.
     *
     * @param ctxt The DeserializationContext to use for deserialization.
     * @param node The JsonNode representing the object to deserialize.
     * @return The deserialized internal type object.
     */
    protected open fun getInternalTypeObject(
        ctxt: DeserializationContext,
        node: JsonNode,
    ): T = ctxt.readTreeAsValue(node, PrincipalSerializationObject::class.java) as T
}

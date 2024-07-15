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

import com.alcosi.lib.security.AccountDetails
import com.alcosi.lib.security.ClientAccountDetails
import com.fasterxml.jackson.annotation.JsonIgnoreProperties
import com.fasterxml.jackson.databind.DeserializationContext
import com.fasterxml.jackson.databind.JsonNode

/**
 * ClientAccountDetailsPrincipalDeSerializer is a class that extends UniversalPrincipalDetailsDeSerializer and is responsible for deserializing
 * ClientAccountDetails objects from JSON.
 *
 * @constructor Creates an instance of ClientAccountDetailsPrincipalDeSerializer.
 */
open class ClientAccountDetailsPrincipalDeSerializer : UniversalPrincipalDetailsDeSerializer<ClientAccountDetailsPrincipalDeSerializer.ClientAccountDetailsSerializationObject>(ClientAccountDetails::class.java) {
    /**
     * ClientAccountDetailsSerializationObject is a class that represents the serialization object for ClientAccountDetails.
     *
     * @property clientId The client ID associated with the account.
     * @constructor Creates an instance of ClientAccountDetailsSerializationObject.
     * @param id The ID of the serialization object.
     * @param authorities The list of authorities associated with the serialization object.
     * @param className The class name of the serialization object.
     * @param type The type of the serialization object.
     */
    @JsonIgnoreProperties(ignoreUnknown = true)
    open class ClientAccountDetailsSerializationObject(
        id: String,
        authorities: List<String>,
        className: String,
        type: String,
        val clientId: String,
    ) : PrincipalSerializationObject(id, authorities, className, type)

    /**
     * Converts a ClientAccountDetailsSerializationObject to an AccountDetails object.
     *
     * @param serializationObject The object to be converted.
     * @return The converted AccountDetails object.
     */
    override fun returnRealObject(serializationObject: ClientAccountDetailsSerializationObject): AccountDetails =
        ClientAccountDetails(serializationObject.id, serializationObject.authorities, serializationObject.clientId, serializationObject.className, serializationObject.additionalProperties)

    /**
     * Retrieves the internal type object from the given JSON node using the provided DeserializationContext.
     *
     * @param ctxt The DeserializationContext to use.
     * @param node The JSON node to deserialize.
     * @return The internal type object.
     */
    override fun getInternalTypeObject(
        ctxt: DeserializationContext,
        node: JsonNode,
    ): ClientAccountDetailsSerializationObject = ctxt.readTreeAsValue(node, ClientAccountDetailsSerializationObject::class.java)
}

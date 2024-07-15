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
import com.alcosi.lib.security.OrganisationAccountDetails
import com.fasterxml.jackson.annotation.JsonIgnoreProperties
import com.fasterxml.jackson.databind.DeserializationContext
import com.fasterxml.jackson.databind.JsonNode

/**
 * OrganisationAccountDetailsPrincipalDeSerializer is a class that extends UniversalPrincipalDetailsDeSerializer
 * and is responsible for deserializing OrganisationAccountDetails objects from JSON.
 *
 * @constructor Creates an instance of OrganisationAccountDetailsPrincipalDeSerializer
 */
open class OrganisationAccountDetailsPrincipalDeSerializer : UniversalPrincipalDetailsDeSerializer<OrganisationAccountDetailsPrincipalDeSerializer.OrganisationAccountDetailsSerializationObject>(OrganisationAccountDetails::class.java) {
    /**
     * OrganisationAccountDetailsSerializationObject is a class that represents the serialization object for the organisation account details.
     *
     * @property organisationId The ID of the organisation.
     * @constructor Creates an instance of OrganisationAccountDetailsSerializationObject.
     * @param id The ID of the account.
     * @param authorities The authorities of the account.
     * @param className The class name of the object.
     * @param type The type of the object.
     */
    @JsonIgnoreProperties(ignoreUnknown = true)
    open class OrganisationAccountDetailsSerializationObject(
        id: String,
        authorities: List<String>,
        className: String,
        type: String,
        val organisationId: String,
    ) : PrincipalSerializationObject(id, authorities, className, type)

    /**
     * Converts an OrganisationAccountDetailsSerializationObject to an AccountDetails object.
     *
     * @param serializationObject The object to be converted.
     * @return The converted AccountDetails object.
     */
    override fun returnRealObject(serializationObject: OrganisationAccountDetailsSerializationObject): AccountDetails =
        OrganisationAccountDetails(serializationObject.id, serializationObject.authorities, serializationObject.organisationId, serializationObject.className, serializationObject.additionalProperties)

    /**
     * Returns the internal type object of OrganisationAccountDetailsSerializationObject by reading the given JSON node using the
     * DeserializationContext.
     *
     * @param ctxt the DeserializationContext object.
     * @param node the JSON node containing the data to be deserialized.
     * @return the OrganisationAccountDetailsSerializationObject.
     */
    override fun getInternalTypeObject(
        ctxt: DeserializationContext,
        node: JsonNode,
    ): OrganisationAccountDetailsSerializationObject = ctxt.readTreeAsValue(node, OrganisationAccountDetailsSerializationObject::class.java)
}

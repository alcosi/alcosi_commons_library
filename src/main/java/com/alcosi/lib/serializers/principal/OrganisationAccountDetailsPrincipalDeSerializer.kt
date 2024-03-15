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

open class OrganisationAccountDetailsPrincipalDeSerializer : UniversalPrincipalDetailsDeSerializer<OrganisationAccountDetailsPrincipalDeSerializer.OrganisationAccountDetailsSerializationObject>(OrganisationAccountDetails::class.java) {
    @JsonIgnoreProperties(ignoreUnknown = true)
    open class OrganisationAccountDetailsSerializationObject(
        id: String,
        authorities: List<String>,
        className: String,
        type: String,
        val organisationId: String,
    ) : PrincipalSerializationObject(id, authorities, className, type)

    override fun returnRealObject(serializationObject: OrganisationAccountDetailsSerializationObject): AccountDetails {
        return OrganisationAccountDetails(serializationObject.id, serializationObject.authorities, serializationObject.organisationId, serializationObject.className)
    }

    override fun getInternalTypeObject(
        ctxt: DeserializationContext,
        node: JsonNode,
    ): OrganisationAccountDetailsSerializationObject = ctxt.readTreeAsValue(node, OrganisationAccountDetailsSerializationObject::class.java)
}

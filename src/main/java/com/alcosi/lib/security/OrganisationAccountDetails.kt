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

package com.alcosi.lib.security

import com.alcosi.lib.serializers.principal.OrganisationAccountDetailsPrincipalDeSerializer
import com.fasterxml.jackson.annotation.JsonIgnoreProperties
import com.fasterxml.jackson.databind.annotation.JsonDeserialize

/**
 * OrganisationAccountDetails is an open class that represents the details of an organization account.
 *
 * @param id The ID of the account.
 * @param authorities The list of authorities associated with the account.
 * @param organisationId The ID of the organization.
 * @param className The class name of the account.
 * @constructor Creates an instance of OrganisationAccountDetails.
 * @extends AccountDetails
 */
@JsonDeserialize(using = OrganisationAccountDetailsPrincipalDeSerializer::class)
@JsonIgnoreProperties(ignoreUnknown = true)
open class OrganisationAccountDetails(
    id: String,
    authorities: List<String>,
    val organisationId: String,
    className: String = OrganisationAccountDetails::class.java.name,
    override val additionalProperties: Map<String, String> = mapOf(),
) : AccountDetails(id, authorities, className)

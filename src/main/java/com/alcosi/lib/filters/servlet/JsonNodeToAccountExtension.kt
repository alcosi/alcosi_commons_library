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

package com.alcosi.lib.filters.servlet

import com.alcosi.lib.objectMapper.MappingHelper
import com.alcosi.lib.objectMapper.mapOneNode
import com.alcosi.lib.security.AccountDetails
import com.alcosi.lib.security.ClientAccountDetails
import com.alcosi.lib.security.OrganisationAccountDetails
import com.fasterxml.jackson.databind.JsonNode
import com.fasterxml.jackson.databind.ObjectMapper

/**
 * Maps the account details from a JsonNode using a MappingHelper.
 *
 * @param mappingHelper The MappingHelper used for mapping.
 * @return The mapped AccountDetails object, or null if the JsonNode is null or the mapping fails.
 */
@Deprecated("Use objectMapper function")
fun JsonNode?.mapAccountDetails(mappingHelper: MappingHelper): AccountDetails?  {
    return if (this == null) {
        null
    } else if (this.hasNonNull("clientId")) {
        mappingHelper.mapOneNode<ClientAccountDetails>(this)
    } else if (this.hasNonNull("organisationId")) {
        mappingHelper.mapOneNode<OrganisationAccountDetails>(this)
    } else {
        mappingHelper.mapOneNode<AccountDetails>(this)
    }
}
/**
 * Maps the account details from a JsonNode using a ObjectMapper.
 *
 * @param mapper The ObjectMapper used for mapping.
 * @return The mapped AccountDetails object, or null if the JsonNode is null or the mapping fails.
 */
fun JsonNode?.mapAccountDetails(mapper:ObjectMapper): AccountDetails?  {
    return if (this == null) {
        null
    } else if (this.hasNonNull("clientId")) {
        mapper.mapOneNode<ClientAccountDetails>(this)
    } else if (this.hasNonNull("organisationId")) {
        mapper.mapOneNode<OrganisationAccountDetails>(this)
    } else {
        mapper.mapOneNode<AccountDetails>(this)
    }
}

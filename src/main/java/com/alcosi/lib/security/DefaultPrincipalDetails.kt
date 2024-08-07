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

import com.alcosi.lib.serializers.principal.DefaultPrincipalDetailsDeSerializer
import com.fasterxml.jackson.annotation.JsonIgnoreProperties
import com.fasterxml.jackson.databind.annotation.JsonDeserialize

/**
 * This class represents default principal details for an authenticated user.
 *
 * @param id The identifier of the user.
 * @param authorities The list of authorities associated with the user.
 * @param className The name of the class representing the user.
 * @param type The type of the user.
 */
@JsonIgnoreProperties(ignoreUnknown = true)
@JsonDeserialize(using = DefaultPrincipalDetailsDeSerializer::class)
open class DefaultPrincipalDetails(
    override val id: String,
    override val authorities: List<String>,
    val className: String,
    override val type: String,
    override val additionalProperties: Map<String, String> = mapOf(),
) : PrincipalDetails

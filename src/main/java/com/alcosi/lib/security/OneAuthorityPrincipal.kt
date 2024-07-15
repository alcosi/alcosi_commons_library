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

import com.fasterxml.jackson.annotation.JsonIgnoreProperties

/**
 * OneAuthorityPrincipal is an abstract class that represents a principal with a single authority.
 * It extends DefaultPrincipalDetails and provides a common implementation for principals with a single authority.
 *
 * @param id The identifier of the user.
 * @param authority The authority associated with the user.
 * @param className The name of the class representing the user.
 * @param type The type of the user.
 */
@JsonIgnoreProperties(ignoreUnknown = true)
abstract class OneAuthorityPrincipal(
    id: String,
    authority: String,
    className: String,
    type: String,
    additionalProperties: Map<String, String> = mapOf(),
) : DefaultPrincipalDetails(id, listOf(authority), className, type, additionalProperties)

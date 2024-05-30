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

import com.alcosi.lib.serializers.principal.UserDetailsPrincipalDeSerializer
import com.fasterxml.jackson.annotation.JsonIgnoreProperties
import com.fasterxml.jackson.databind.annotation.JsonDeserialize

/**
 * UserDetails is a class that represents user details.
 *
 * @property id The identifier of the user.
 * @property className The name of the class representing the user. Default value is the name of UserDetails class.
 *
 * @constructor Creates an instance of UserDetails.
 * @param id The identifier of the user.
 * @param className The name of the class representing the user. Default value is the name of UserDetails class.
 *
 * @see OneAuthorityPrincipal
 * @see UserDetailsPrincipalDeSerializer
 */
@JsonDeserialize(using = UserDetailsPrincipalDeSerializer::class)
@JsonIgnoreProperties(ignoreUnknown = true)
class UserDetails(
    id: String,
    className: String = UserDetails::class.java.name,
) : OneAuthorityPrincipal(id, "PERMISSION_USER", className, "USER")

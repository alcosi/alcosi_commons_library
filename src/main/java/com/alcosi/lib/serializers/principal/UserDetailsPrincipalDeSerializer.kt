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

import com.alcosi.lib.security.UserDetails

/**
 * UserDetailsPrincipalDeSerializer is a class that extends UniversalPrincipalDetailsDeSerializer. It provides deserialization for UserDetails objects.
 *
 * @constructor Creates an instance of UserDetailsPrincipalDeSerializer.
 */
open class UserDetailsPrincipalDeSerializer : UniversalPrincipalDetailsDeSerializer<UniversalPrincipalDetailsDeSerializer.PrincipalSerializationObject>(UserDetails::class.java) {
    /**
     * Returns a UserDetails object based on the provided PrincipalSerializationObject.
     *
     * @param serializationObject The object used for serialization.
     * @return The UserDetails object created from serializationObject.
     */
    override fun returnRealObject(serializationObject: PrincipalSerializationObject): UserDetails = UserDetails(serializationObject.id, serializationObject.className, serializationObject.additionalProperties)
}

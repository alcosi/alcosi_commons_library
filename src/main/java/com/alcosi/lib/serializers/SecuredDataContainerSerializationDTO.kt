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

package com.alcosi.lib.serializers

import com.fasterxml.jackson.annotation.JsonCreator
import com.fasterxml.jackson.annotation.JsonProperty

/**
 * SecuredDataContainerSerializationDTO is a data class representing a serialized version of a SecuredDataContainer object.
 * It is used for serialization and deserialization of SecuredDataContainer objects to and from JSON.
 *
 * @property type The type of the SecuredDataContainer, which can be either BYTE_ARRAY or STRING.
 * @property value The encrypted value of the SecuredDataContainer as a String.
 * @property originalLength The original length of the encrypted value before encryption.
 */
data class SecuredDataContainerSerializationDTO
    @JsonCreator
    constructor(
        @JsonProperty("type") val type: TYPE,
        @JsonProperty("value") val value: String,
        @JsonProperty("originalLength") val originalLength: Int,
    ) {
    /**
     * Enum representing the type of a SecuredDataContainer.
     */
    enum class TYPE {
            BYTE_ARRAY,
            STRING,
        }
    }

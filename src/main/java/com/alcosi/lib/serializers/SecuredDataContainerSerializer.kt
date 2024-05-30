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

import com.alcosi.lib.secured.container.SecuredDataByteArray
import com.alcosi.lib.secured.container.SecuredDataContainer
import com.alcosi.lib.secured.container.SecuredDataString
import com.alcosi.lib.secured.encrypt.SensitiveComponent
import com.fasterxml.jackson.core.JsonGenerator
import com.fasterxml.jackson.databind.SerializerProvider
import com.fasterxml.jackson.databind.ser.std.StdSerializer

/**
 * This class is responsible for serializing objects of type SecuredDataContainer to JSON.
 * It extends the StdSerializer class and overrides the serialize method.
 *
 * @constructor Creates an instance of SecuredDataContainerSerializer.
 */
open class SecuredDataContainerSerializer : StdSerializer<SecuredDataContainer<*>>(SecuredDataContainer::class.java) {
    /**
     * This method is responsible for serializing objects of type SecuredDataContainer to JSON.
     * It takes a nullable value of type SecuredDataContainer, a JsonGenerator, and a SerializerProvider as parameters.
     * If the value is null, it writes a null value using the JsonGenerator.
     * If the value is not null, it determines the type of the SecuredDataContainer and creates a SecuredDataContainerSerializationDTO object*/
    override fun serialize(
        value: SecuredDataContainer<*>?,
        gen: JsonGenerator,
        provider: SerializerProvider,
    ) {
        if (value == null) {
            gen.writeNull()
        } else {
            val type =
                when (value) {
                    is SecuredDataByteArray -> SecuredDataContainerSerializationDTO.TYPE.BYTE_ARRAY
                    is SecuredDataString -> SecuredDataContainerSerializationDTO.TYPE.STRING
                    else -> {
                        throw IllegalArgumentException("Unsupported type ${value.className}")
                    }
                }
            val container = SecuredDataContainerSerializationDTO(type, sensitiveComponent.serialize(value.encrypted)!!, value.originalLength)
            gen.writeObject(container)
        }
    }

    companion object {
        /**
         * This variable represents a sensitive component used for handling and manipulating sensitive data.
         *
         * @property sensitiveComponent The sensitive component instance.
         * @see SensitiveComponent
         */
        protected lateinit var sensitiveComponent: SensitiveComponent

        /**
         * Sets the provided [SensitiveComponent] instance to be used for serializing sensitive data.
         *
         * @param c The [SensitiveComponent] instance to be set.
         */
        fun setSensitiveComponentPublic(c: SensitiveComponent) {
            sensitiveComponent = c
        }
    }
}

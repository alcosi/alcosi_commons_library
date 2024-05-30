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
import com.fasterxml.jackson.core.JsonParser
import com.fasterxml.jackson.databind.DeserializationContext
import com.fasterxml.jackson.databind.JavaType
import com.fasterxml.jackson.databind.deser.std.StdDeserializer

/**
 * Deserializer class for SecuredDataContainer.
 * It extends StdDeserializer and overrides the deserialize method.
 */
open class SecuredDataContainerDeSerializer : StdDeserializer<SecuredDataContainer<*>>(SecuredDataContainer::class.java) {
    /**
     * Deserializes a JSON string into a SecuredDataContainer object.
     *
     * @param p The JsonParser used for parsing the JSON string.
     * @param ctxt The DeserializationContext.
     * @return The deserialized SecuredDataContainer object, or null if p is null or the container is null.
     */
    override fun deserialize(
        p: JsonParser?,
        ctxt: DeserializationContext?,
    ): SecuredDataContainer<*>? {
        if (p == null) {
            return getNullValue(ctxt)
        } else {
            val container = ctxt!!.readValue(p, SecuredDataContainerSerializationDTO::class.java)
            return if (container == null) {
                getNullValue(ctxt)
            } else {
                val encodedValue = sensitiveComponent.deserialize(container.value)!!
                val securedDataByteArray = SecuredDataByteArray(container.originalLength, encodedValue)
                val result =
                    when (container.type) {
                        SecuredDataContainerSerializationDTO.TYPE.BYTE_ARRAY -> securedDataByteArray
                        SecuredDataContainerSerializationDTO.TYPE.STRING -> SecuredDataString(securedDataByteArray)
                    }
                result
            }
        }
    }

    /**
     * Retrieves the type of a SecuredDataContainer based on the class.
     *
     * @param c The JavaType class to determine the type of.
     * @return The type of the SecuredDataContainer.
     * @throws IllegalStateException if the SecuredDataContainer type is incorrect.
     */
    protected open fun getTypeByClass(c: JavaType): SecuredDataContainerSerializationDTO.TYPE {
        return if (c.isTypeOrSubTypeOf(SecuredDataString::class.java)) {
            SecuredDataContainerSerializationDTO.TYPE.STRING
        } else if (c.isTypeOrSubTypeOf(SecuredDataByteArray::class.java)) {
            SecuredDataContainerSerializationDTO.TYPE.BYTE_ARRAY
        } else {
            throw IllegalStateException("Wrong SecuredDataContainer type :${c.rawClass.simpleName}")
        }
    }



    companion object {
        /**
         * Represents a sensitive component used for deserialization and encryption*/
        protected lateinit var sensitiveComponent: SensitiveComponent

        /**
         * Sets the provided [SensitiveComponent] instance as the sensitive component
         * to be used by the [SecuredDataContainerSerializer] and [SecuredDataContainerDeSerializer] classes.
         *
         * @param c The [SensitiveComponent] instance to set.
         */
        fun setSensitiveComponentPublic(c: SensitiveComponent) {
            sensitiveComponent = c
        }
    }


}

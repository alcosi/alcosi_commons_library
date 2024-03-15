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

open class SecuredDataContainerDeSerializer : StdDeserializer<SecuredDataContainer<*>>(SecuredDataContainer::class.java) {
//    , ContextualDeserializer
//    protected open val nullByteArray = SecuredDataByteArray(0, null)
//    protected open val nullString = SecuredDataString(SecuredDataByteArray(0, null))
//    protected var currentClass: JavaType? = null

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

    protected open fun getTypeByClass(c: JavaType): SecuredDataContainerSerializationDTO.TYPE {
        return if (c.isTypeOrSubTypeOf(SecuredDataString::class.java)) {
            SecuredDataContainerSerializationDTO.TYPE.STRING
        } else if (c.isTypeOrSubTypeOf(SecuredDataByteArray::class.java)) {
            SecuredDataContainerSerializationDTO.TYPE.BYTE_ARRAY
        } else {
            throw IllegalStateException("Wrong SecuredDataContainer type :${c.rawClass.simpleName}")
        }
    }

//    override fun getNullValue(ctxt: DeserializationContext?): SecuredDataContainer<*> {
//        return if (currentClass == null) {
//            super.getNullValue(ctxt)
//        } else {
//            when (getTypeByClass(currentClass!!)) {
//                SecuredDataContainerSerializationDTO.TYPE.BYTE_ARRAY -> nullByteArray
//                SecuredDataContainerSerializationDTO.TYPE.STRING -> nullString
//            }
//        }
//    }

    companion object {
        protected lateinit var sensitiveComponent: SensitiveComponent

        fun setSensitiveComponentPublic(c: SensitiveComponent) {
            sensitiveComponent = c
        }
    }

//    override fun createContextual(
//        ctxt: DeserializationContext?,
//        property: BeanProperty?,
//    ): JsonDeserializer<*> {
//        val new = SecuredDataContainerDeSerializer()
//        new.currentClass = property?.type
//        return new
//    }
}

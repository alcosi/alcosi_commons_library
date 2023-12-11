/*
 * Copyright (c) 2024  Alcosi Group Ltd. and affiliates.
 *
 * Portions of this software are licensed as follows:
 *
 *     All content that resides under the "alcosi" and "atomicon" or “deploy” directories of this repository, if that directory exists, is licensed under the license defined in "LICENSE.TXT".
 *
 *     All third-party components incorporated into this software are licensed under the original license provided by the owner of the applicable component.
 *
 *     Content outside of the above-mentioned directories or restrictions above is available under the MIT license as defined below.
 *
 *
 *
 * MIT License
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy of this software and associated documentation files (the "Software"), to deal in the Software without restriction, including without limitation the rights to use, copy, modify, merge, publish, distribute, sublicense, and/or sell copies of the Software, and to permit persons to whom the Software is urnished to do so, subject to the following conditions:
 *
 *
 *
 * The above copyright notice and this permission notice shall be included in all copies or substantial portions of the Software.
 *
 *
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY, FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM, OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE SOFTWARE.
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

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
import com.fasterxml.jackson.core.JsonGenerator
import com.fasterxml.jackson.databind.SerializerProvider
import com.fasterxml.jackson.databind.ser.std.StdSerializer

class SecuredDataContainerSerializer : StdSerializer<SecuredDataContainer<*>>(SecuredDataContainer::class.java) {
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
        protected lateinit var sensitiveComponent: SensitiveComponent

        fun setSensitiveComponentPublic(c: SensitiveComponent) {
            sensitiveComponent = c
        }
    }
}

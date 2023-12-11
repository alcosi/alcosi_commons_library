/*
 * Copyright (c) 2023  Alcosi Group Ltd. and affiliates.
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

package com.alcosi.lib.secured.container

import com.alcosi.lib.secured.encrypt.EncryptionConfig
import com.alcosi.lib.secured.encrypt.EncryptionContainerConfig
import com.alcosi.lib.secured.encrypt.key.KeyProvider
import com.fasterxml.jackson.databind.ObjectMapper
import com.fasterxml.jackson.module.kotlin.readValue
import org.apache.commons.codec.binary.Hex
import org.junit.jupiter.api.Assertions
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.ExtendWith
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.context.annotation.Import
import org.springframework.test.context.TestPropertySource
import org.springframework.test.context.junit.jupiter.SpringExtension

@TestPropertySource(locations = ["classpath:secured/application-test.properties"])
@ExtendWith(SpringExtension::class)
@Import(EncryptionConfig::class, EncryptionContainerConfig::class)
class ContainerBytesSerializationTest {
    @Autowired
    lateinit var keyProvider: KeyProvider
    val objectMapper: ObjectMapper = ObjectMapper()

    @Test
    fun testBytesEmpty() {
        val containerOriginal = SecuredDataByteArray.create(ByteArray(0), keyProvider.key(KeyProvider.MODE.ENCRYPT))
        val serialized = objectMapper.writeValueAsString(containerOriginal)
        val containerDeserialized = objectMapper.readValue(serialized, SecuredDataByteArray::class.java)
        Assertions.assertEquals(containerOriginal, containerDeserialized)
    }

    @Test
    fun testBytesWithValue() {
        val containerOriginal = SecuredDataByteArray.create(Hex.decodeHex("AB17"), keyProvider.key(KeyProvider.MODE.ENCRYPT))
        val serialized = objectMapper.writeValueAsString(containerOriginal)
        val containerDeserialized = objectMapper.readValue(serialized, SecuredDataByteArray::class.java)
        Assertions.assertEquals(containerOriginal, containerDeserialized)
    }
}

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

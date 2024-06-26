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

package com.alcosi.lib.secured.config

import com.alcosi.lib.secured.SecuredConstants.EMPTY_TEXT
import com.alcosi.lib.secured.SecuredConstants.LARGE_TEXT
import com.alcosi.lib.secured.SecuredConstants.SMALL_TEXT
import com.alcosi.lib.secured.container.SecuredDataByteArray
import com.alcosi.lib.secured.container.SecuredDataString
import com.alcosi.lib.secured.encrypt.EncryptionConfig
import com.alcosi.lib.secured.encrypt.EncryptionContainerConfig
import com.alcosi.lib.secured.encrypt.SensitiveComponent
import com.alcosi.lib.secured.encrypt.key.KeyProvider
import com.fasterxml.jackson.databind.ObjectMapper
import org.apache.commons.codec.binary.Base64
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
class SensitiveConfigTest {
    @Autowired
    lateinit var keyProvider: KeyProvider

    @Autowired
    lateinit var sensitiveComponent: SensitiveComponent
    val objectMapper = ObjectMapper()

    data class TestTuple(val one: Any, val two: Any, val three: Any)

    @Test
    fun testUnencryptedEmptyByteArray() {
        val containerOriginal = SecuredDataByteArray.create(ByteArray(0), keyProvider.key(KeyProvider.MODE.ENCRYPT))
        val serialized = objectMapper.writeValueAsString(containerOriginal)
        val decrypted = sensitiveComponent.decrypt(serialized, keyProvider.key(KeyProvider.MODE.DECRYPT))
        Assertions.assertEquals("\"\"", decrypted)
    }

    @Test
    fun testUnencryptedNonEmptyByteArray() {
        val originalValue = SMALL_TEXT.toByteArray()
        val originalValueBase64 = Base64.encodeBase64String(originalValue)
        val containerOriginal = SecuredDataByteArray.create(originalValue, keyProvider.key(KeyProvider.MODE.ENCRYPT))
        val serialized = objectMapper.writeValueAsString(containerOriginal)
        val decrypted = sensitiveComponent.decrypt(serialized, keyProvider.key(KeyProvider.MODE.DECRYPT))
        Assertions.assertEquals("\"$originalValueBase64\"", decrypted)
    }

    @Test
    fun testUnencryptedEmptyString() {
        val containerOriginal = SecuredDataString.create(EMPTY_TEXT, keyProvider.key(KeyProvider.MODE.ENCRYPT))
        val serialized = objectMapper.writeValueAsString(containerOriginal)
        val decrypted = sensitiveComponent.decrypt(serialized, keyProvider.key(KeyProvider.MODE.DECRYPT))
        Assertions.assertEquals("\"\"", decrypted)
    }

    @Test
    fun testUnencryptedNonEmptyString() {
        val originalValue = SMALL_TEXT
        val containerOriginal = SecuredDataString.create(originalValue, keyProvider.key(KeyProvider.MODE.ENCRYPT))
        val serialized = objectMapper.writeValueAsString(containerOriginal)
        val decrypted = sensitiveComponent.decrypt(serialized, keyProvider.key(KeyProvider.MODE.DECRYPT))
        Assertions.assertEquals("\"$originalValue\"", decrypted)
    }

    @Test
    fun testUnencryptedNonEmptyStringStruct() {
        val containerOriginal = SecuredDataString.create(SMALL_TEXT, keyProvider.key(KeyProvider.MODE.ENCRYPT))
        val containerOriginal2 = SecuredDataString.create(LARGE_TEXT, keyProvider.key(KeyProvider.MODE.ENCRYPT))
        val tuple = TestTuple(containerOriginal, "SMTH", containerOriginal2)
        val serialized = objectMapper.writeValueAsString(tuple)
        val decrypted = sensitiveComponent.decrypt(serialized, keyProvider.key(KeyProvider.MODE.DECRYPT))
        val largeTextJson = objectMapper.writeValueAsString(LARGE_TEXT)
        val expected = "{\"one\":\"$SMALL_TEXT\",\"two\":\"SMTH\",\"three\":$largeTextJson}"
        Assertions.assertEquals(expected, decrypted)
    }

    @Test
    fun testUnencryptedNonEmptyStringStructArray() {
        val containerOriginal = SecuredDataString.create(SMALL_TEXT, keyProvider.key(KeyProvider.MODE.ENCRYPT))
        val containerOriginal2 = SecuredDataString.create(LARGE_TEXT, keyProvider.key(KeyProvider.MODE.ENCRYPT))
        val tuple = TestTuple(containerOriginal, "SMTH", containerOriginal2)
        val serialized = objectMapper.writeValueAsString(listOf(tuple, tuple, "SMTH", tuple))
        val decrypted = sensitiveComponent.decrypt(serialized, keyProvider.key(KeyProvider.MODE.DECRYPT))
        val largeTextJson = objectMapper.writeValueAsString(LARGE_TEXT)
        val expectedTuple = "{\"one\":\"$SMALL_TEXT\",\"two\":\"SMTH\",\"three\":$largeTextJson}"
        val expected = "[$expectedTuple,$expectedTuple,\"SMTH\",$expectedTuple]"
        Assertions.assertEquals(expected, decrypted)
    }
}

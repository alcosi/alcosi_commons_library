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
    fun testUnencryptedNullByteArray() {
        val containerOriginal = SecuredDataByteArray.create(null, keyProvider.key(KeyProvider.MODE.ENCRYPT))
        val serialized = objectMapper.writeValueAsString(containerOriginal)
        val decrypted = sensitiveComponent.decrypt(serialized, keyProvider.key(KeyProvider.MODE.DECRYPT))
        Assertions.assertEquals("null", decrypted)
    }

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
    fun testUnencryptedNullString() {
        val containerOriginal = SecuredDataString.create(null, keyProvider.key(KeyProvider.MODE.ENCRYPT))
        val serialized = objectMapper.writeValueAsString(containerOriginal)
        val decrypted = sensitiveComponent.decrypt(serialized, keyProvider.key(KeyProvider.MODE.DECRYPT))
        Assertions.assertEquals("null", decrypted)
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

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

package com.alcosi.lib.secured.encrypt

import com.alcosi.lib.secured.SecuredConstants.AES_TEST_KEY
import com.alcosi.lib.secured.SecuredConstants.LARGE_TEXT
import com.alcosi.lib.secured.SecuredConstants.SMALL_TEXT
import com.alcosi.lib.secured.encrypt.encryption.aes.AesDecrypter
import com.alcosi.lib.secured.encrypt.encryption.aes.AesEncrypter
import com.alcosi.lib.secured.encrypt.key.KeyProvider
import com.alcosi.lib.secured.encrypt.key.PropertiesKeyProvider
import org.junit.jupiter.api.Assertions.assertArrayEquals
import org.junit.jupiter.api.Assertions.assertNull
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.ExtendWith
import org.springframework.test.context.junit.jupiter.SpringExtension
import java.util.logging.Logger

@ExtendWith(SpringExtension::class)
class AESTest {
    private val keyProvider: PropertiesKeyProvider = PropertiesKeyProvider(AES_TEST_KEY, AES_TEST_KEY)
    private val aes256Encrypter: AesEncrypter = AesEncrypter()
    private val aes256Decrypter: AesDecrypter = AesDecrypter()

    @Test
    fun testEncryptDecryptEmpty() {
        val encrypted = aes256Encrypter.encrypt(null, keyProvider.key(KeyProvider.MODE.ENCRYPT))
        assertNull(encrypted)
        val decrypted = aes256Decrypter.decrypt(null, keyProvider.key(KeyProvider.MODE.DECRYPT))
        assertNull(decrypted)
    }

    @Test
    fun testEncryptDecryptSmall() {
        val originalText = SMALL_TEXT.toByteArray()
        testEncryptAndDecrypt(originalText)
    }

    @Test
    fun testEncryptDecryptLarge() {
        testEncryptAndDecrypt(LARGE_TEXT.toByteArray())
    }

    fun testEncryptAndDecrypt(text: ByteArray) {
        var time = System.currentTimeMillis()
        val encrypted = aes256Encrypter.encrypt(text, keyProvider.key(KeyProvider.MODE.ENCRYPT))
        logger.info("Encryption took ${System.currentTimeMillis() - time}ms")
        time = System.currentTimeMillis()
        val decrypted = aes256Decrypter.decrypt(encrypted, keyProvider.key(KeyProvider.MODE.DECRYPT))
        logger.info("Decryption took ${System.currentTimeMillis() - time}ms")
        // Assert that the decrypted text matches the original
        assertArrayEquals(text, decrypted)
    }

    companion object {
        val logger = Logger.getLogger(this.javaClass.name)
    }
}

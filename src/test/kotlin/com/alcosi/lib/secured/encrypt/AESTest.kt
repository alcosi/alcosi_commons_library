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

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

import com.alcosi.lib.secured.SecuredConstants
import com.alcosi.lib.secured.SecuredConstants.SMALL_TEXT
import com.alcosi.lib.secured.encrypt.encryption.rsa.RsaDecrypter
import com.alcosi.lib.secured.encrypt.encryption.rsa.RsaEncrypter
import com.alcosi.lib.secured.encrypt.key.KeyProvider
import com.alcosi.lib.secured.encrypt.key.PropertiesKeyProvider
import org.apache.commons.codec.binary.Hex
import org.junit.jupiter.api.Assertions.assertArrayEquals
import org.junit.jupiter.api.Assertions.assertNull
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.ExtendWith
import org.springframework.test.context.junit.jupiter.SpringExtension
import java.security.KeyPairGenerator
import java.util.logging.Logger

@ExtendWith(SpringExtension::class)
class RSATest {
    private val rsaEncrypter: RsaEncrypter = RsaEncrypter()
    private val rsaDecrypter: RsaDecrypter = RsaDecrypter()
    private val keyProvider: PropertiesKeyProvider

    init {
        val key = generateKeyPair()
        keyProvider = PropertiesKeyProvider(key.second, key.first)
    }

    fun generateKeyPair(): Pair<String, String> {
        val keyPairGenerator = KeyPairGenerator.getInstance("RSA")
        keyPairGenerator.initialize(512)
        val keyPair = keyPairGenerator.generateKeyPair()
        val publicKey = keyPair.public.encoded
        val privateKey = keyPair.private.encoded
        val pair = Hex.encodeHexString(publicKey) to Hex.encodeHexString(privateKey)
        logger.info(pair.second)
        logger.info(pair.first)
        return pair
    }

    @Test
    fun testEncryptDecryptEmpty() {
        val encrypted = rsaEncrypter.encrypt(null, keyProvider.key(KeyProvider.MODE.ENCRYPT))
        assertNull(encrypted)
        val decrypted = rsaDecrypter.decrypt(null, keyProvider.key(KeyProvider.MODE.DECRYPT))
        assertNull(decrypted)
    }

    @Test
    fun testEncryptDecryptSmall() {
        val originalText = SMALL_TEXT.toByteArray()
        testEncryptAndDecrypt(originalText)
    }

    @Test
    fun testEncryptDecryptLarge() {
        testEncryptAndDecrypt(SecuredConstants.LARGE_TEXT.toByteArray())
    }

    fun testEncryptAndDecrypt(text: ByteArray) {
        var time = System.currentTimeMillis()
        val encrypted = rsaEncrypter.encrypt(text, keyProvider.key(KeyProvider.MODE.ENCRYPT))
        logger.info("Encryption took ${System.currentTimeMillis() - time}ms")
        time = System.currentTimeMillis()
        val decrypted = rsaDecrypter.decrypt(encrypted, keyProvider.key(KeyProvider.MODE.DECRYPT))
        logger.info("Decryption took ${System.currentTimeMillis() - time}ms")
        // Assert that the decrypted text matches the original
        assertArrayEquals(text, decrypted)
    }

    companion object {
        val logger = Logger.getLogger(this.javaClass.name)
    }
}

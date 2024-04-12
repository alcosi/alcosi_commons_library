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

package com.alcosi.lib.secured.encrypt.encryption.rsa

import com.alcosi.lib.secured.encrypt.encryption.Encrypter
import com.alcosi.lib.secured.encrypt.encryption.rsa.RsaDecrypter.Companion.executor
import java.security.PublicKey
import java.security.interfaces.RSAPublicKey
import java.security.spec.X509EncodedKeySpec
import java.util.concurrent.Executors
import javax.crypto.Cipher

open class RsaEncrypter : Encrypter {
    override fun encrypt(
        data: ByteArray?,
        key: ByteArray,
    ): ByteArray? {
        if (data == null) {
            return null
        }
        if (data.isEmpty()) {
            return ByteArray(0)
        }
        val keySpec = X509EncodedKeySpec(key)
        val pubKey = Rsa.keyFactory.generatePublic(keySpec) as RSAPublicKey
        val bitLength = pubKey.modulus.bitLength()
        val chunkSize = bitLength / 8
        val chunkSizeEncrypt = chunkSize - 11
        val length = data.size
        val loops = (length / chunkSizeEncrypt) + if (length % chunkSizeEncrypt > 0) 1 else 0
        val result = ByteArray(loops * chunkSize)
        for (i in 0..<loops) {
            val startIndex = i * chunkSizeEncrypt
            val endIndex = minOf(startIndex + chunkSizeEncrypt, length)
            val chunk = data.copyOfRange(startIndex, endIndex)
            val encryptedChunk = encryptChunck(pubKey, chunk)
            encryptedChunk.copyInto(destination = result, destinationOffset = chunkSize * i)
        }
        return result
    }

    protected open fun encryptChunck(
        pubKey: PublicKey,
        data: ByteArray,
    ): ByteArray {
        val cipher = Rsa.createCipher()
        cipher.init(Cipher.ENCRYPT_MODE, pubKey)
        return cipher.doFinal(data)
    }
}

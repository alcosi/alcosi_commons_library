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

import com.alcosi.lib.secured.encrypt.encryption.Decrypter
import java.security.interfaces.RSAPrivateKey
import java.security.spec.PKCS8EncodedKeySpec
import javax.crypto.Cipher

class RsaDecrypter : Decrypter {
    override fun decrypt(
        data: ByteArray?,
        key: ByteArray,
    ): ByteArray? {
        if (data == null) {
            return null
        }
        if (data.isEmpty()) {
            return ByteArray(0)
        }
        val keySpec = PKCS8EncodedKeySpec(key)
        val privKey = Rsa.keyFactory.generatePrivate(keySpec) as RSAPrivateKey
        val bitLength = privKey.modulus.bitLength()
        val chunkSize = bitLength / 8
        val chunkSizeEncrypt = chunkSize - 11
        val length = data.size
        val loops = length / chunkSize - 1
        if (length % chunkSize > 0) {
            throw IllegalArgumentException("Length of encoded data is not correct for $bitLength key")
        }
        val lastChunkStart = loops * chunkSize
        val lastChunkDecrypted = decryptChunk(privKey, data.copyOfRange(lastChunkStart, length))
        val lastChunkSize = lastChunkDecrypted.size
        val result = ByteArray((loops * chunkSizeEncrypt) + lastChunkSize)
        for (i in 0..<loops) {
            val startIndex = i * chunkSize
            val endIndex = startIndex + chunkSize
            val chunk = data.copyOfRange(startIndex, endIndex)
            val decryptedChunk = decryptChunk(privKey, chunk)
            decryptedChunk.copyInto(destination = result, destinationOffset = chunkSizeEncrypt * i)
        }
        lastChunkDecrypted.copyInto(destination = result, destinationOffset = chunkSizeEncrypt * loops)
        return result
    }

    private fun decryptChunk(
        privKey: RSAPrivateKey,
        data: ByteArray,
    ): ByteArray {
        val cipher = Rsa.createCipher()
        cipher.init(Cipher.DECRYPT_MODE, privKey)
        return cipher.doFinal(data)
    }
}

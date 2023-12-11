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

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

import com.alcosi.lib.secured.encrypt.encryption.Encrypter
import java.security.PublicKey
import java.security.interfaces.RSAPublicKey
import java.security.spec.X509EncodedKeySpec
import javax.crypto.Cipher

class RsaEncrypter : Encrypter {
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

    protected fun encryptChunck(
        pubKey: PublicKey,
        data: ByteArray,
    ): ByteArray {
        val cipher = Rsa.createCipher()
        cipher.init(Cipher.ENCRYPT_MODE, pubKey)
        return cipher.doFinal(data)
    }
}

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
import java.util.concurrent.Executors
import javax.crypto.Cipher

/**
 * The RsaDecrypter class is used to decrypt data using the RSA encryption algorithm.
 */
open class RsaDecrypter : Decrypter {
    /**
     * Decrypts the provided data using the given key.
     *
     * @param data The encrypted data to be decrypted, represented as a ByteArray. Can be null.
     * @param key The key used for decryption, represented as a ByteArray.
     * @return The decrypted data as a ByteArray, or null if the provided data is null.
     * @throws IllegalArgumentException If the length of the encoded data is not correct for the key length.
     */
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
        val threadLocal= ThreadLocal.withInitial{createCipher(privKey)}
        val lastChunkDecryptedFuture =executor.submit<ByteArray> { decryptChunk( data.copyOfRange(lastChunkStart, length),threadLocal)}
        val lastChunkDecrypted=lastChunkDecryptedFuture.get()
        val lastChunkSize = lastChunkDecrypted.size
        val result = ByteArray((loops * chunkSizeEncrypt) + lastChunkSize)
        lastChunkDecrypted .copyInto(destination = result, destinationOffset = chunkSizeEncrypt * loops)
        val results = (0 until loops).map { i ->
            executor.submit {
                val startIndex = i * chunkSize
                val endIndex = startIndex + chunkSize
                val chunk = data.copyOfRange(startIndex, endIndex)
                val decryptedChunk = decryptChunk( chunk,threadLocal)
                decryptedChunk.copyInto(destination = result, destinationOffset = chunkSizeEncrypt * i)
            }
        }

        results.map { it.get() }
        return result
    }
    /**
     * Decrypts a chunk of data using the specified cipher instance.
     *
     * @param data The chunk of data to decrypt.
     * @param tl The ThreadLocal instance containing the Cipher instance.
     * @return The decrypted chunk of data as a byte array.
     */
    protected open fun decryptChunk(
        data: ByteArray,
        tl:ThreadLocal<Cipher>
    ): ByteArray {
        return tl.get().doFinal(data)
    }
    /**
     * Creates a Cipher instance for decrypting data using the given RSAPrivateKey.
     *
     * @param privKey the RSAPrivateKey used for decryption
     * @return a Cipher instance initialized for decryption with the provided private key
     */
    protected open fun createCipher(privKey: RSAPrivateKey): Cipher {
        val cipher = Rsa.createCipher()
        cipher.init(Cipher.DECRYPT_MODE, privKey)
        return cipher
    }
    /**
     * The `Companion` class represents a companion object that contains utility methods and properties related to decryption.
     *
     * @property executor The executor to be used for background decryption tasks.
     */
    companion object {
        protected open val executor = Executors.newFixedThreadPool(Runtime.getRuntime().availableProcessors())
    }
}

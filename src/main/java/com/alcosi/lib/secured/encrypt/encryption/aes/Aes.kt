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

package com.alcosi.lib.secured.encrypt.encryption.aes

import org.bouncycastle.jce.provider.BouncyCastleProvider
import java.security.Security
import javax.crypto.Cipher
import javax.crypto.spec.IvParameterSpec
import javax.crypto.spec.SecretKeySpec

/**
 * The Aes object provides functionality for AES encryption and decryption.
 */
object Aes {
    /**
     * A constant string representing the provider used in the cryptographic operations.
     */
    val PROVIDER = "BC"
    /**
     * The algorithm used for encryption and decryption.
     *
     * @property ALGORITHM The algorithm name.
     */
    val ALGORITHM = "AES"
    /**
     * This constant variable defines the transformation algorithm used for encrypting and decrypting data.
     * It specifies the encryption algorithm (AES), mode (CBC), and padding (PKCS5Padding).
     */
    val TRANSFORMATION = "AES/CBC/PKCS5Padding"
    /**
     * Represents an initialization vector (IV) parameter specification for cryptographic operations.
     *
     * @property iv The byte array containing the IV.
     */
    val ivParameterSpec = IvParameterSpec(ByteArray(16))
    /**
     * Creates a new instance of Cipher with the specified transformation and provider.
     *
     * @return the newly created Cipher object.
     * @throws NoSuchAlgorithmException if the specified transformation is not available.
     * @throws NoSuchProviderException if the specified provider is not available.
     */
    fun createCipher(): Cipher {
        return Cipher.getInstance(TRANSFORMATION, PROVIDER)
    }
    /**
     * Initializes a Cipher object with the provided key and mode.
     *
     * @param key The key used for encryption or decryption, represented as a ByteArray.
     * @param mode The mode of operation for the cipher. Should be one of Cipher.ENCRYPT_MODE or Cipher.DECRYPT_MODE.
     * @return The initialized Cipher object.
     */
    fun initCipher(
        key: ByteArray,
        mode: Int,
    ): Cipher {
        val cipher = createCipher()
        val secretKey = SecretKeySpec(key, ALGORITHM)
        cipher.init(mode, secretKey, ivParameterSpec)
        return cipher
    }

    init {
        Security.addProvider(BouncyCastleProvider())
    }
}

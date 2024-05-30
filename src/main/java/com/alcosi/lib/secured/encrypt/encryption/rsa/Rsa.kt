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


import org.bouncycastle.jce.provider.BouncyCastleProvider
import java.security.KeyFactory
import java.security.Security
import javax.crypto.Cipher

/**
 * Rsa is a utility object that provides methods for RSA encryption and decryption.
 */
object Rsa {
    init {
        Security.addProvider(BouncyCastleProvider())
    }
    /**
     * Represents the algorithm used for encryption.
     * This variable should contain the name of the algorithm used for encryption.
     *
     * @see <a href="https*/
    val ALGORITHM = "RSA"
    /**
     * The TRANSFORMATION variable represents the transformation algorithm used for encryption and decryption.
     *
     * The value of TRANSFORMATION is set to "RSA".
     */
    val TRANSFORMATION = "RSA"
    /**
     * This variable represents an instance of the KeyFactory class obtained by calling the getInstance method with a specified algorithm.
     * The algorithm used to obtain the instance must be provided as a string value.
     * @see KeyFactory
     */
    val keyFactory = KeyFactory.getInstance(ALGORITHM,)

    /**
     * Creates a new instance of the Cipher class using the specified transformation algorithm.
     *
     * @return A new instance of [Cipher] class.
     * @throws NoSuchAlgorithmException If the requested transformation algorithm is not available.
     * @throws NoSuchPaddingException If the requested padding mechanism is not available.
     */
    fun createCipher(): Cipher {
        return Cipher.getInstance(TRANSFORMATION)
    }
}

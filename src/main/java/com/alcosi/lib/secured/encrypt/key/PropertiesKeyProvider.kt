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

package com.alcosi.lib.secured.encrypt.key

import org.apache.commons.codec.binary.Hex

/**
 * Provides a key for encryption or decryption using a private and public key stored as byte arrays.
 *
 * @param privKey The private key as a hexadecimal string.
 * @param publicKey The public key as a hexadecimal string.
 */
open class PropertiesKeyProvider(privKey: String, publicKey: String) : KeyProvider {
    protected open val privKeyBytes = Hex.decodeHex(privKey)
    protected open val pubKeyBytes = Hex.decodeHex(publicKey)
    /**
     * Return the appropriate key based on the provided mode.
     *
     * @param mode The mode indicating whether to return the encryption key or the decryption key.
     * @return The key as a ByteArray. If the mode is `MODE.ENCRYPT`, it returns the public key as a ByteArray.
     * If the mode is `MODE.DECRYPT`, it returns the private key as a ByteArray.
     */
    override fun key(mode: KeyProvider.MODE): ByteArray {
        return when (mode) {
            KeyProvider.MODE.ENCRYPT -> pubKeyBytes
            KeyProvider.MODE.DECRYPT -> privKeyBytes
        }
    }
}

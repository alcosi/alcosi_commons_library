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

package com.alcosi.lib.secured.container

import com.alcosi.lib.secured.encrypt.encryption.Decrypter
import com.alcosi.lib.secured.encrypt.encryption.Encrypter

/**
 * Represents a secured data container for byte arrays.
 * Implements [SecuredDataContainer] interface.
 *
 * @property originalLength The length of the original byte array.
 * @property encrypted The encrypted byte array.
 * @property className The class name of the byte array.
 */
data class SecuredDataByteArray(
    override val originalLength: Int,
    override val encrypted: ByteArray,
    override val className: String = "ByteArray",
) : SecuredDataContainer<ByteArray> {
    /**
     * Decodes the encrypted data using the provided key.
     *
     * @param key The key used for decryption.
     * @return The decoded data as a ByteArray.
     */
    override fun decoded(key: ByteArray): ByteArray {
        return decrypter.decrypt(encrypted, key)!!
    }

    /**
     * Returns a string representation of the SecuredDataByteArray object.
     *
     * If the `encrypted` field is null, it returns "<SecuredData:null:0>".
     * If the `encrypted` field is empty, it returns "<SecuredData:0>".
     * Otherwise, it returns "<SecuredData:className:originalLength>".
     *
     * @return The string representation of the SecuredDataByteArray object.
     */
    override fun toString(): String {
        if (encrypted == null) {
            return "<SecuredData:null:0>"
        }
        if (encrypted.isEmpty()) {
            return "<SecuredData:0>"
        }
        return "<SecuredData:$className:$originalLength>"
    }
    /**
     * Compares this SecuredDataByteArray object with the specified object for equality.
     *
     * @param other the object to compare with this SecuredDataByteArray
     * @return true if the specified object is equal to this SecuredDataByteArray,
     *         false otherwise.
     */
    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false
        other as SecuredDataByteArray
        if (originalLength != other.originalLength) return false
        if (encrypted != null) {
            if (other.encrypted == null) return false
            if (!encrypted.contentEquals(other.encrypted)) return false
        } else if (other.encrypted != null) {
            return false
        }
        if (className != other.className) return false
        return true
    }
    /**
     * Calculates the hash code for the object.
     *
     * @return The hash code value for the object.
     */
    override fun hashCode(): Int {
        var result = originalLength
        result = 31 * result + (encrypted?.contentHashCode() ?: 0)
        result = 31 * result + className.hashCode()
        return result
    }
    /**
     * The `Companion` class provides static utility methods for creating `SecuredDataByteArray` objects,
     * setting the encrypter and decrypter services, and performing encryption operations on data.
     */
    companion object {
        fun create(
            originalValue: ByteArray,
            key: ByteArray,
        ): SecuredDataByteArray {
            val encrypted = encrypter.encrypt(originalValue, key)
            val originalLength = originalValue?.size ?: 0
            return SecuredDataByteArray(originalLength, encrypted!!)
        }
        /**
         * Encrypter class is used to perform encryption operations on data.
         * It provides various methods to encrypt and decrypt data using different encryption algorithms.
         */
        private lateinit var encrypter: Encrypter
        /**
         * Represents a Decrypter.
         *
         *
         * This class is responsible for decrypting data.
         *
         *
         * @property decrypter The instance of the Decrypter.
         */
        private lateinit var decrypter: Decrypter
        /**
         * Sets the provided Encrypter service to be used for encryption.
         *
         * @param service The Encrypter service to be set.
         */
        fun setEncrypter(service: Encrypter) {
            encrypter = service
        }
        /**
         * Sets the decrypter service to be used for decrypting data.
         *
         * @param service The decrypter service instance.
         */
        fun setDecrypter(service: Decrypter) {
            decrypter = service
        }
    }
}

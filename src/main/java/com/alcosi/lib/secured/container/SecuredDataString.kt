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

/**
 * Represents a secured data string.
 *
 * This class encapsulates a secured data byte array and provides methods to work with it as a string.
 * It implements the SecuredDataContainer interface with String as the generic type.
 *
 * @param delegate the underlying secured data byte array
 *
 * @property className the name of the class
 * @property encrypted indicates whether the data is encrypted
 * @property originalLength the original length of the data
 */
data class SecuredDataString(val delegate: SecuredDataByteArray) : SecuredDataContainer<String> {
    /**
     * Represents the name of the class.
     */
    override val className = delegate.className
    /**
     * The `encrypted` variable represents the encrypted data stored as a byte array.
     * It is obtained from the `delegate` property of the enclosing `SecuredDataString` class.
     *
     * @property encrypted The encrypted data stored as a byte array.
     */
    override val encrypted = delegate.encrypted
    /**
     * Represents the original length of a variable.
     *
     * Warning: The documentation for this class is automatically generated from the given Kotlin code.
     * Do not modify it manually.
     *
     * @property originalLength The length of the variable.
     */
    override val originalLength = delegate.originalLength
    /**
     * Decodes the encrypted data using the provided key.
     *
     * @param key The key used for decryption.
     * @return The decoded data as a String.
     */
    override fun decoded(key: ByteArray): String {
        return delegate.decoded(key).toString(Charsets.ISO_8859_1)
    }
    /**
     * Compares this SecuredDataString object with the specified object for equality.
     *
     * @param other the object to compare with this SecuredDataString
     * @return true if the specified object is equal to this SecuredDataString, false otherwise.
     */
    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false

        other as SecuredDataString

        return delegate == other.delegate
    }
    /**
     * Calculates the hash code for the object.
     *
     * @return The hash code value for the object.
     */
    override fun hashCode(): Int {
        return delegate.hashCode()
    }
    /**
     * Provides a static factory method for creating instances of the [SecuredDataString] class.
     *
     * @property create A static factory method that takes an original value and a key to create a new [SecuredDataString] instance.
     */
    companion object {
        /**
         * Creates a SecuredDataString object by encrypting the original value using the provided key.
         *
         * @param originalValue The original value to be encrypted.
         * @param key The key used for encryption.
         * @return The SecuredDataString object containing the encrypted value.
         */
        fun create(
            originalValue: String,
            key: ByteArray,
        ): SecuredDataString {
            val byteContainer = SecuredDataByteArray.create(originalValue.toByteArray(Charsets.ISO_8859_1), key)
            return (SecuredDataString(byteContainer))
        }
    }
}

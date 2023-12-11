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

package com.alcosi.lib.secured.container

import com.alcosi.lib.secured.encrypt.encryption.Decrypter
import com.alcosi.lib.secured.encrypt.encryption.Encrypter

data class SecuredDataByteArray(
    override val originalLength: Int,
    override val encrypted: ByteArray,
    override val className: String = "ByteArray",
) :
    SecuredDataContainer<ByteArray> {
    override fun decoded(key: ByteArray): ByteArray {
        return decrypter.decrypt(encrypted, key)!!
    }

    override fun toString(): String {
        if (encrypted == null) {
            return "<SecuredData:null:0>"
        }
        if (encrypted.isEmpty()) {
            return "<SecuredData:0>"
        }
        return "<SecuredData:$className:$originalLength>"
    }

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

    override fun hashCode(): Int {
        var result = originalLength
        result = 31 * result + (encrypted?.contentHashCode() ?: 0)
        result = 31 * result + className.hashCode()
        return result
    }

    companion object {
        fun create(
            originalValue: ByteArray,
            key: ByteArray,
        ): SecuredDataByteArray {
            val encrypted = encrypter.encrypt(originalValue, key)
            val originalLength = originalValue?.size ?: 0
            return SecuredDataByteArray(originalLength, encrypted!!)
        }

        private lateinit var encrypter: Encrypter
        private lateinit var decrypter: Decrypter

        fun setEncrypter(service: Encrypter) {
            encrypter = service
        }

        fun setDecrypter(service: Decrypter) {
            decrypter = service
        }
    }
}

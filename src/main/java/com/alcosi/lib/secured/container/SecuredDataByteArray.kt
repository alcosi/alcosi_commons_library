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

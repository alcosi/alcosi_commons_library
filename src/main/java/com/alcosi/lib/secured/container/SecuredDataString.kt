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

data class SecuredDataString(val delegate: SecuredDataByteArray) : SecuredDataContainer<String> {
    override val className = delegate.className
    override val encrypted = delegate.encrypted
    override val originalLength = delegate.originalLength

    override fun decoded(key: ByteArray): String {
        return delegate.decoded(key).toString(Charsets.ISO_8859_1)
    }

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false

        other as SecuredDataString

        return delegate == other.delegate
    }

    override fun hashCode(): Int {
        return delegate.hashCode()
    }

    companion object {
        fun create(
            originalValue: String,
            key: ByteArray,
        ): SecuredDataString {
            val byteContainer = SecuredDataByteArray.create(originalValue.toByteArray(Charsets.ISO_8859_1), key)
            return (SecuredDataString(byteContainer))
        }
    }
}

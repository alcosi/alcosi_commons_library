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

import com.alcosi.lib.secured.encrypt.encryption.Encrypter
import javax.crypto.Cipher

class AesEncrypter : Encrypter {
    override fun encrypt(
        value: ByteArray?,
        key: ByteArray,
    ): ByteArray? {
        if (value == null) {
            return null
        }
        if (value.isEmpty()) {
            return ByteArray(0)
        }
        val cipher = Aes.initCipher(key, Cipher.ENCRYPT_MODE)
        val encrypted = cipher.doFinal(value)
        return encrypted
    }
}

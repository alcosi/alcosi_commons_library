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

object Aes {
    val PROVIDER = "BC"
    val ALGORITHM = "AES"
    val TRANSFORMATION = "AES/CBC/PKCS5Padding"
    val ivParameterSpec = IvParameterSpec(ByteArray(16))

    fun createCipher(): Cipher {
        return Cipher.getInstance(TRANSFORMATION, PROVIDER)
    }

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

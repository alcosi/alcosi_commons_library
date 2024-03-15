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

object Rsa {
    val ALGORITHM = "RSA"
    val TRANSFORMATION = "RSA"
    val keyFactory = KeyFactory.getInstance(ALGORITHM)

    fun createCipher(): Cipher {
        return Cipher.getInstance(TRANSFORMATION)
    }

    init {
        Security.addProvider(BouncyCastleProvider())
    }
}

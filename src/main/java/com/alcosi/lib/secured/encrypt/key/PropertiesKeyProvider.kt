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

class PropertiesKeyProvider(privKey: String, publicKey: String) : KeyProvider {
    private val privKeyBytes = Hex.decodeHex(privKey)
    private val pubKeyBytes = Hex.decodeHex(publicKey)

    override fun key(mode: KeyProvider.MODE): ByteArray {
        return when (mode) {
            KeyProvider.MODE.ENCRYPT -> pubKeyBytes
            KeyProvider.MODE.DECRYPT -> privKeyBytes
        }
    }
}

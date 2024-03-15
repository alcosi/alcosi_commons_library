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

package com.alcosi.lib.utils

import java.util.regex.Pattern

open class PrepareHexService {
    val walletPattern: Pattern = Pattern.compile("^[0-9a-f]{40}$")
    val hexPattern: Pattern = Pattern.compile("^([0-9a-f][0-9a-f])+$")

    open fun isValidWalletAddress(wallet: String): Boolean {
        val prepared = prepareHexInternal(wallet)
        return walletPattern.matcher(prepared).matches()
    }

    open fun isValidHex(hex: String): Boolean {
        val prepared = prepareHexInternal(hex)
        return hexPattern.matcher(prepared).matches()
    }

    open fun prepareAddr(wallet: String?): String {
        requireNotNull(wallet) { "Wallet is null" }
        val prepared = prepareHexInternal(wallet)
        require(walletPattern.matcher(prepared).matches()) { wallet + "is invalid wallet address!" }
        return prepared
    }

    open fun prepareHexNoMatcher(hex: String?): String {
        requireNotNull(hex) { "Hex string is null" }
        if (hasHexPrefix(hex) && hex.length == 2) return "00"
        return prepareHexInternal(hex)
    }

    open fun prepareHex(hex: String?): String {
        requireNotNull(hex) { "Hex string is null" }
        val prepared = prepareHexInternal(hex)
        require(hexPattern.matcher(prepared).matches()) { hex + "is invalid hex string!" }
        return prepared
    }

    protected open fun prepareHexInternal(wallet: String): String = removeHexPrefix(wallet).lowercase()

    protected open fun removeHexPrefix(wallet: String): String {
        return if (hasHexPrefix(wallet)) {
            wallet.substring(2)
        } else {
            wallet
        }
    }

    protected open fun hasHexPrefix(wallet: String): Boolean {
        return wallet.length > 1 && wallet[0] == '0' && (wallet[1] == 'x' || wallet[1] == 'X')
    }
}

/*
 * Copyright (c) 2023  Alcosi Group Ltd. and affiliates.
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

package com.alcosi.lib.utils


import org.springframework.stereotype.Service
import java.util.regex.Pattern

@Service
open class PrepareHexService() {
    val walletPattern: Pattern = Pattern.compile("^[0-9a-f]{40}$")
    val hexPattern: Pattern = Pattern.compile("^([0-9a-f][0-9a-f])+$")

    fun isValidWalletAddress(wallet: String): Boolean {
        val prepared = prepareHexInternal(wallet)
        return walletPattern.matcher(prepared).matches()
    }

    fun isValidHex(hex: String): Boolean {
        val prepared = prepareHexInternal(hex)
        return hexPattern.matcher(prepared).matches()
    }

    fun prepareAddr(wallet: String?): String {
        requireNotNull(wallet) { "Wallet is null" }
        val prepared = prepareHexInternal(wallet)
        require(walletPattern.matcher(prepared).matches()) { wallet + "is invalid wallet address!" }
        return prepared
    }

    fun prepareHexNoMatcher(hex: String?): String {
        requireNotNull(hex) { "Hex string is null" }
        if (hasHexPrefix(hex) && hex.length == 2) return "00";
        return prepareHexInternal(hex);
    }

    fun prepareHex(hex: String?): String {
        requireNotNull(hex) { "Hex string is null" }
        val prepared = prepareHexInternal(hex)
        require(hexPattern.matcher(prepared).matches()) { hex + "is invalid hex string!" }
        return prepared
    }

    private fun prepareHexInternal(wallet: String): String = removeHexPrefix(wallet).lowercase()

    private fun removeHexPrefix(wallet: String): String {
        return if (hasHexPrefix(wallet)) {
            wallet.substring(2)
        } else wallet;
    }

    private fun hasHexPrefix(wallet: String): Boolean {
        return wallet.length > 1 && wallet[0] == '0' && (wallet[1] == 'x' || wallet[1] == 'X')
    }
}
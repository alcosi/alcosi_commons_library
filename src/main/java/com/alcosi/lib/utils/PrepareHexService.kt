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

/**
 * The PrepareHexService class provides methods to validate and prepare hexadecimal strings and wallet addresses.
 *
 * @property walletPattern The pattern used for validating wallet addresses.
 * @property hexPattern The pattern used for validating hexadecimal strings.
 */
open class PrepareHexService {
    val walletPattern: Pattern = Pattern.compile("^[0-9a-f]{40}$")
    val hexPattern: Pattern = Pattern.compile("^([0-9a-f][0-9a-f])+$")
    /**
     * Checks if the given wallet address is valid.
     *
     * @param wallet The wallet address to be checked.
     * @return `true` if the wallet address is valid, `false` otherwise.
     */
    open fun isValidWalletAddress(wallet: String): Boolean {
        val prepared = prepareHexInternal(wallet)
        return walletPattern.matcher(prepared).matches()
    }
    /**
     * Checks if the given string represents a valid hexadecimal value.
     *
     * @param hex The string to be checked.
     * @return `true` if the string is valid hexadecimal, `false` otherwise.
     */
    open fun isValidHex(hex: String): Boolean {
        val prepared = prepareHexInternal(hex)
        return hexPattern.matcher(prepared).matches()
    }
    /**
     * Prepares a wallet address by validating and formatting it.
     *
     * @param wallet The wallet address to prepare. Cannot be null.
     * @return The prepared wallet address as a string.
     * @throws IllegalArgumentException If the wallet address is null or invalid.
     */
    open fun prepareAddr(wallet: String?): String {
        requireNotNull(wallet) { "Wallet is null" }
        val prepared = prepareHexInternal(wallet)
        require(walletPattern.matcher(prepared).matches()) { wallet + "is invalid wallet address!" }
        return prepared
    }
    /**
     * Prepares a hexadecimal matcher string.
     *
     * @param hex The hexadecimal string to prepare. Must not be null.
     * @return The prepared hexadecimal matcher string.
     *
     * @throws IllegalArgumentException If the hex string is null.
     */
    open fun prepareHexNoMatcher(hex: String?): String {
        requireNotNull(hex) { "Hex string is null" }
        if (hasHexPrefix(hex) && hex.length == 2) return "00"
        return prepareHexInternal(hex)
    }
    /**
     * Prepares a hexadecimal string by removing the prefix, converting it to lowercase, and checking its validity.
     *
     * @param hex The hexadecimal string to be prepared.
     * @return The prepared hexadecimal string.
     * @throws IllegalArgumentException if the *hex* parameter is null or if the prepared hexadecimal string is invalid.
     */
    open fun prepareHex(hex: String?): String {
        requireNotNull(hex) { "Hex string is null" }
        val prepared = prepareHexInternal(hex)
        require(hexPattern.matcher(prepared).matches()) { hex + "is invalid hex string!" }
        return prepared
    }
    /**
     * Prepares the hexadecimal representation of a wallet address by removing the hex prefix and converting to lowercase.
     *
     * @param wallet The wallet address to prepare.
     * @return The prepared hexadecimal representation of the wallet address.
     */
    protected open fun prepareHexInternal(wallet: String): String = removeHexPrefix(wallet).lowercase()
    /**
     * Removes the hex prefix from a given wallet address.
     *
     * @param wallet The wallet address with or without a hex prefix.
     * @return The wallet address without the hex prefix.
     */
    protected open fun removeHexPrefix(wallet: String): String {
        return if (hasHexPrefix(wallet)) {
            wallet.substring(2)
        } else {
            wallet
        }
    }
    /**
     * Checks if the given wallet string has a hex prefix.
     *
     * @param wallet the wallet string to be checked
     * @return true if the wallet string has a hex prefix, false otherwise
     */
    protected open fun hasHexPrefix(wallet: String): Boolean {
        return wallet.length > 1 && wallet[0] == '0' && (wallet[1] == 'x' || wallet[1] == 'X')
    }
}

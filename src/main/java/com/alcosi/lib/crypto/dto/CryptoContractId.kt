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

package com.alcosi.lib.crypto.dto

import com.fasterxml.jackson.annotation.JsonAlias
import com.fasterxml.jackson.annotation.JsonProperty

/**
 * The CryptoContractId class represents the ID of a crypto contract. It contains the address and chain ID.
 *
 * @property address The address of the contract.
 * @property chainId The chain ID of the contract.
 */

data class CryptoContractId(
    @JsonProperty("address") val address: String,
    @JsonProperty("chainId")
    @JsonAlias("chain_id") val chainId: Long,
) {
    override fun toString(): String = "$chainId:$address"
}

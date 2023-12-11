/*
 * Copyright (c) 2024  Alcosi Group Ltd. and affiliates.
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

package com.alcosi.lib.crypto.contracts

import com.alcosi.lib.crypto.dto.CryptoContractId
import com.alcosi.lib.crypto.nodes.CryptoNodesAdminServiceHolder
import com.alcosi.lib.executors.SchedulerTimer
import com.alcosi.lib.utils.PrepareHexService
import org.web3j.crypto.Credentials
import org.web3j.protocol.Web3j
import org.web3j.tx.Contract
import org.web3j.tx.RawTransactionManager
import org.web3j.tx.TransactionManager
import org.web3j.tx.gas.ContractGasProvider
import java.time.Duration
import java.time.LocalDateTime
import java.util.logging.Level
import java.util.logging.Logger
import java.util.stream.Collectors

open class SmartContractCreator(
    val lifetime: Duration,
    val pk: String,
    val clearDelay: Duration,
    val gasProvider: ContractGasProvider,
    val prepareWalletComponent: PrepareHexService,
    val nodesAdminService: CryptoNodesAdminServiceHolder,
) {
    val contractMap: MutableMap<ContractKey, ContractPair> = HashMap()

    @JvmRecord
    data class ContractKey(
        val credentials: Credentials,
        val address: String,
        val chainId: Int,
        val contractType: Class<*>,
    )

    @JvmRecord
    data class ContractPair(val contract: Contract, val usedTime: LocalDateTime = LocalDateTime.now())

    open fun <T : Contract> build(
        contractId: CryptoContractId,
        c: Class<T>,
    ): T {
        val credential = Credentials.create(pk)
        return getContract(
            contractId.chainId.toInt(),
            "0x" + prepareWalletComponent.prepareAddr(contractId.address),
            credential,
            c,
        )
    }

    protected open fun <T : Contract> getContract(
        chainId: Int,
        address: String,
        credential: Credentials,
        c: Class<T>,
    ): T {
        val contractKey = ContractKey(credential, address, chainId, c)
        return if (!contractMap.containsKey(contractKey)) {
            val tm = getTM(credential, chainId)
            val load =
                c.getDeclaredConstructor(
                    String::class.java,
                    Web3j::class.java,
                    TransactionManager::class.java,
                    ContractGasProvider::class.java,
                )
            load.isAccessible = true
            val contract = load.newInstance(address, nodesAdminService[chainId], tm, gasProvider) as Contract
            contractMap[contractKey] = ContractPair(contract)
            contract as T
        } else {
            val contractPair = contractMap[contractKey]
            if (!c.isAssignableFrom(contractPair!!.contract.javaClass)) {
                contractMap.remove(contractKey)
                return getContract(chainId, address, credential, c)
            }
            contractMap[contractKey] = ContractPair(contractPair.contract)
            contractPair.contract as T
        }
    }

    open fun getTM(
        credentials: Credentials,
        chainId: Int,
    ): TransactionManager {
        return RawTransactionManager(nodesAdminService[chainId], credentials, chainId.toLong())
    }

    protected open val scheduler =
        object : SchedulerTimer(clearDelay, "ClearContracts", Level.FINEST) {
            override fun startBatch() {
                val now = LocalDateTime.now()
                val old =
                    contractMap.entries
                        .filter { (key, value) ->
                            value
                                .usedTime
                                .plus(lifetime)
                                .isBefore(now)
                        }.map { it.key }
                val removed =
                    old
                        .stream()
                        .map { key: ContractKey -> key to contractMap.remove(key) }
                        .map { p -> "${p.first}:${p.second}" }
                        .collect(Collectors.joining(";"))
                if (removed.isNotBlank()) {
                    Companion.logger.warning("Smart contract for $removed removed ")
                }
            }
        }

    companion object {
        val logger = Logger.getLogger(this::class.java.name)
    }
}

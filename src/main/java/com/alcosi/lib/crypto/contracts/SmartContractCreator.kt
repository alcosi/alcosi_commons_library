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

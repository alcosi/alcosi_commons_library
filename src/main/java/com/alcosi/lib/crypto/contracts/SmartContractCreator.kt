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
import com.alcosi.lib.utils.PrepareHexService
import io.github.breninsul.javatimerscheduler.registry.SchedulerType
import io.github.breninsul.javatimerscheduler.registry.TaskSchedulerRegistry
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

/**
 * The SmartContractCreator class is responsible for creating and managing
 * smart contract instances.
 *
 * @property lifetime The duration for which a smart contract instance
 *     remains active.
 * @property pk The private key used for creating and accessing smart
 *     contracts.
 * @property clearDelay The duration after which inactive smart contract
 *     instances are cleared.
 * @property gasProvider The gas provider used for contract deployments and
 *     transactions.
 * @property prepareWalletComponent The service used for validating and
 *     preparing wallet addresses.
 * @property nodesAdminService The service holder for accessing admin
 *     nodes.
 * @property contractMap The map storing the created smart contract
 *     instances.
 */
open class SmartContractCreator(
    val lifetime: Duration,
    val pk: String,
    val clearDelay: Duration,
    val gasProvider: ContractGasProvider,
    val prepareWalletComponent: PrepareHexService,
    val nodesAdminService: CryptoNodesAdminServiceHolder,
) {
    val contractMap: MutableMap<ContractKey, ContractPair> = HashMap()
    init {
        TaskSchedulerRegistry.registerTypeTask(SchedulerType.VIRTUAL_WAIT, "ClearContracts", clearDelay, clearDelay, this::class, Level.FINEST) { clearContracts() }
    }

    /**
     * The ContractKey class represents a key used for identifying a contract.
     *
     * @property credentials The credentials used for contract interaction.
     * @property address The address of the contract.
     * @property chainId The chain ID of the contract.
     * @property contractType The type of the contract.
     */
    @JvmRecord
    data class ContractKey(
        val credentials: Credentials,
        val address: String,
        val chainId: Int,
        val contractType: Class<*>,
    )

    /**
     * The ContractPair class represents a pair of a contract and the time it
     * was used.
     *
     * @param contract The contract instance.
     * @param usedTime The time the contract was used. Default value is the
     *     current system time.
     * @property contract The contract instance.
     * @property usedTime The time the contract was used. Default value is the
     *     current system time when the ContractPair object is created.
     */
    @JvmRecord
    data class ContractPair(val contract: Contract, val usedTime: LocalDateTime = LocalDateTime.now())

    /**
     * Builds a contract instance of type T using the specified contract ID and
     * class.
     *
     * @param contractId The CryptoContractId representing the contract ID.
     * @param c The class representing the contract type.
     * @return An instance of type T representing the built contract.
     * @throws RuntimeException if the contract cannot be built.
     * @throws IllegalArgumentException if the contractId or c parameters are
     *     null.
     */
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

    /**
     * Retrieves a contract instance of type T using the specified chain ID,
     * address, credentials, and class.
     *
     * @param chainId The chain ID of the contract.
     * @param address The address of the contract.
     * @param credential The credentials used for contract interaction.
     * @param c The class representing the contract type.
     * @return An instance of type T representing the contract.
     */
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

    /**
     * Retrieves a TransactionManager instance for the given chain ID and
     * credentials.
     *
     * @param credentials The credentials used for contract interaction.
     * @param chainId The chain ID of the contract.
     * @return The TransactionManager instance.
     */
    open fun getTM(
        credentials: Credentials,
        chainId: Int,
    ): TransactionManager {
        return RawTransactionManager(nodesAdminService[chainId], credentials, chainId.toLong())
    }

    /**
     * Clears the expired contracts from the contract map.
     */
    protected open fun clearContracts() {
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
            logger.warning("Smart contract for $removed removed ")
        }
    }

    /**
     * The Companion class is responsible for providing a logger instance.
     */
    companion object {
        /**
         * The logger variable is an instance of the Logger class used for logging messages.
         */
        val logger = Logger.getLogger(this::class.java.name)
    }
}

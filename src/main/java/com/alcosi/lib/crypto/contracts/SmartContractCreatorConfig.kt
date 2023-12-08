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

package com.alcosi.lib.crypto.contracts

import org.springframework.beans.factory.annotation.Value
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty
import org.springframework.boot.autoconfigure.condition.ConditionalOnSingleCandidate
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.scheduling.annotation.Scheduled
import org.web3j.tx.Contract
import org.web3j.tx.gas.ContractGasProvider
import org.web3j.tx.gas.DefaultGasProvider
import com.alcosi.lib.crypto.nodes.CryptoNodesAdminServiceHolder
import com.alcosi.lib.synchronisation.SynchronizationService
import com.alcosi.lib.utils.PrepareHexService
import java.time.Duration

@ConditionalOnClass(Scheduled::class,Contract::class)
@ConditionalOnProperty(
    prefix = "common-lib.smart_contract_creator",
    name = arrayOf("disabled"),
    matchIfMissing = true,
    havingValue = "false"
)
@Configuration
class SmartContractCreatorConfig {
    @ConditionalOnSingleCandidate(ContractGasProvider::class)
    fun getGasProvider():ContractGasProvider{
        return DefaultGasProvider()
    }

    @Bean
    fun getSmartContractCreator(
        @Value("\${common-lib.lifetime.smart_contract_creator:10m}") lifetime: Duration,
        @Value("\${common-lib.crypto.address.pk:#{T(com.alcosi.lib.crypto.contracts.SmartContractCreator).Companion.generateRandomPk()}}")
        pk: String,
        gasProvider: ContractGasProvider,
        prepareWalletComponent: PrepareHexService,
        nodesAdminService: CryptoNodesAdminServiceHolder,
    ): SmartContractCreator {
        return SmartContractCreator(lifetime,pk,gasProvider,prepareWalletComponent,nodesAdminService);
    }
}
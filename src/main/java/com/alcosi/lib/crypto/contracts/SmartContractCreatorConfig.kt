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

import com.alcosi.lib.crypto.nodes.CryptoNodesAdminServiceHolder
import com.alcosi.lib.utils.PrepareHexService
import org.springframework.boot.autoconfigure.AutoConfiguration
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty
import org.springframework.boot.context.properties.EnableConfigurationProperties
import org.springframework.context.annotation.Bean
import org.springframework.scheduling.annotation.Scheduled
import org.web3j.tx.Contract
import org.web3j.tx.gas.ContractGasProvider

@ConditionalOnClass(Scheduled::class, Contract::class)
@ConditionalOnProperty(
    prefix = "common-lib.crypto.smart-contract-creator",
    name = ["disabled"],
    matchIfMissing = true,
    havingValue = "false",
)
@AutoConfiguration
@EnableConfigurationProperties(SmartContractCreatorProperties::class)
class SmartContractCreatorConfig {
    @Bean
    @ConditionalOnMissingBean(SmartContractCreator::class)
    fun getSmartContractCreator(
        properties: SmartContractCreatorProperties,
        gasProvider: ContractGasProvider,
        prepareWalletComponent: PrepareHexService,
        nodesAdminService: CryptoNodesAdminServiceHolder,
    ): SmartContractCreator {
        return SmartContractCreator(
            properties.lifetime,
            properties.pk,
            properties.clearDelay,
            gasProvider,
            prepareWalletComponent,
            nodesAdminService,
        )
    }
}

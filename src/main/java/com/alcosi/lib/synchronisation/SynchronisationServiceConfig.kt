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

package com.alcosi.lib.synchronisation

import io.github.breninsul.synchronizationstarter.service.local.LocalClearDecorator
import io.github.breninsul.synchronizationstarter.service.local.LocalSynchronizationService
import org.springframework.boot.autoconfigure.AutoConfiguration
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty
import org.springframework.boot.context.properties.EnableConfigurationProperties
import org.springframework.context.annotation.Bean
import org.springframework.scheduling.annotation.Scheduled
import java.time.Duration

/**
 * Configuration class for the SynchronizationService.
 *
 * This class is responsible for configuring and providing the
 * SynchronizationService bean. The SynchronizationService
 * bean manages locks for synchronization purposes.
 */
@ConditionalOnClass(Scheduled::class)
@ConditionalOnProperty(prefix = "common-lib.synchronisation", name = ["disabled"], matchIfMissing = true, havingValue = "false")
@AutoConfiguration
@EnableConfigurationProperties(SynchronisationProperties::class)
class SynchronisationServiceConfig {
    /**
     * Retrieves the SynchronizationService bean.
     *
     * This method is responsible for retrieving the SynchronizationService
     * bean. If no bean is found, a new instance of SynchronizationService
     * is created using the provided synchronisationProperties and returned.
     *
     * @param synchronisationProperties The synchronisation properties used to
     *     configure the SynchronizationService bean.
     * @return The SynchronizationService bean.
     */
    @Bean
    @ConditionalOnMissingBean(SynchronizationService::class)
    fun getSynchronizationService(synchronisationProperties: SynchronisationProperties): SynchronizationService {
        val local = LocalSynchronizationService(synchronisationProperties.lockTimeout)
        val cleared = LocalClearDecorator(synchronisationProperties.lockTimeout.plus(Duration.ofHours(1)), synchronisationProperties.lockTimeout, synchronisationProperties.clearDelay, local)

        return SynchronizationService(cleared)
    }
}

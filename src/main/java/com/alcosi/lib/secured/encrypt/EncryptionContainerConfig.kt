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

package com.alcosi.lib.secured.encrypt

import com.alcosi.lib.secured.container.SecuredDataByteArray
import com.alcosi.lib.secured.encrypt.encryption.Decrypter
import com.alcosi.lib.secured.encrypt.encryption.Encrypter
import com.alcosi.lib.serializers.SecuredDataContainerDeSerializer
import com.alcosi.lib.serializers.SecuredDataContainerSerializer
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.autoconfigure.AutoConfiguration
import org.springframework.boot.autoconfigure.condition.ConditionalOnBean
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty

@ConditionalOnProperty(
    prefix = "common-lib.secured",
    name = ["disabled"],
    havingValue = "false",
    matchIfMissing = true,
)
@AutoConfiguration
class EncryptionContainerConfig {
    @ConditionalOnBean(Encrypter::class)
    @Autowired
    fun setEncrypterToSecuredDataByteArray(encrypter: Encrypter) {
        SecuredDataByteArray.setEncrypter(encrypter)
    }

    @Autowired
    @ConditionalOnBean(Decrypter::class)
    fun setDecrypterToSecuredDataByteArray(decrypter: Decrypter) {
        SecuredDataByteArray.setDecrypter(decrypter)
    }

    @Autowired
    @ConditionalOnBean(SensitiveComponent::class)
    fun setSensitiveComponentToSerializers(sensitiveComponent: SensitiveComponent) {
        SecuredDataContainerSerializer.setSensitiveComponentPublic(sensitiveComponent)
        SecuredDataContainerDeSerializer.setSensitiveComponentPublic(sensitiveComponent)
    }
}

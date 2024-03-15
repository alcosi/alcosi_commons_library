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

import com.alcosi.lib.filters.servlet.HeaderHelper
import com.alcosi.lib.secured.encrypt.encryption.Decrypter
import com.alcosi.lib.secured.encrypt.encryption.Encrypter
import com.alcosi.lib.secured.encrypt.encryption.aes.AesDecrypter
import com.alcosi.lib.secured.encrypt.encryption.aes.AesEncrypter
import com.alcosi.lib.secured.encrypt.encryption.rsa.RsaDecrypter
import com.alcosi.lib.secured.encrypt.encryption.rsa.RsaEncrypter
import com.alcosi.lib.secured.encrypt.key.HttpRequestKeyProvider
import com.alcosi.lib.secured.encrypt.key.KeyProvider
import com.alcosi.lib.secured.encrypt.key.PropertiesKeyProvider
import com.fasterxml.jackson.databind.ObjectMapper
import org.springframework.boot.autoconfigure.AutoConfiguration
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty
import org.springframework.boot.context.properties.EnableConfigurationProperties
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Primary

@ConditionalOnProperty(
    prefix = "common-lib.secured",
    name = ["disabled"],
    havingValue = "false",
    matchIfMissing = true,
)
@AutoConfiguration
@EnableConfigurationProperties(EncryptionProperties::class)
class EncryptionConfig {
    @ConditionalOnProperty(
        prefix = "common-lib.secured",
        name = ["private-key", "public-key"],
        matchIfMissing = false,
    )
    @Primary
    @ConditionalOnMissingBean(KeyProvider::class)
    @Bean("ServiceDataEncryptionPropertiesKeyProvider")
    fun getServiceDataEncryptionPropertiesSyncKeyProvider(properties: EncryptionProperties): KeyProvider {
        return PropertiesKeyProvider(properties.privateKey, properties.publicKey)
    }

    @ConditionalOnProperty(
        prefix = "common-lib.secured",
        name = ["uri", "access-key"],
        matchIfMissing = false,
    )
    @ConditionalOnMissingBean(KeyProvider::class)
    @Bean("ServiceDataEncryptionHttpRequestKeyProvider")
    fun getServiceDataEncryptionHttpRequestKeyProvider(
        properties: EncryptionProperties,
        helper: HeaderHelper,
        sensitiveComponent: SensitiveComponent,
    ): KeyProvider {
        return HttpRequestKeyProvider(sensitiveComponent, helper, properties.accessKey, properties.uri)
    }

    @ConditionalOnProperty(
        prefix = "common-lib.secured",
        name = ["mode"],
        havingValue = "AES",
        matchIfMissing = true,
    )
    @Bean("AESServiceDataEncryptionEncrypter")
    @ConditionalOnMissingBean(Encrypter::class)
    fun getAESServiceDataEncryptionEncrypter(): Encrypter {
        return AesEncrypter()
    }

    @ConditionalOnProperty(
        prefix = "common-lib.secured",
        name = ["mode"],
        havingValue = "AES",
        matchIfMissing = true,
    )
    @Bean("AESServiceDataEncryptionDecrypter")
    @ConditionalOnMissingBean(Decrypter::class)
    fun getAESServiceDataEncryptionDecrypter(): Decrypter {
        return AesDecrypter()
    }

    @ConditionalOnProperty(
        prefix = "common-lib.secured",
        name = ["mode"],
        havingValue = "RSA",
        matchIfMissing = false,
    )
    @Bean("RSAServiceDataEncryptionEncrypter")
    @ConditionalOnMissingBean(Encrypter::class)
    fun getRSAServiceDataEncryptionEncrypter(): Encrypter {
        return RsaEncrypter()
    }

    @ConditionalOnProperty(
        prefix = "common-lib.secured",
        name = ["mode"],
        havingValue = "RSA",
        matchIfMissing = false,
    )
    @Bean("RSAServiceDataEncryptionDecrypter")
    @ConditionalOnMissingBean(Decrypter::class)
    fun getRSAServiceDataEncryptionDecrypter(): Decrypter {
        return RsaDecrypter()
    }

    @Bean("SensitiveComponent")
    @ConditionalOnMissingBean(SensitiveComponent::class)
    fun getSensitiveComponent(): SensitiveComponent {
        return SensitiveComponent(ObjectMapper())
    }
}

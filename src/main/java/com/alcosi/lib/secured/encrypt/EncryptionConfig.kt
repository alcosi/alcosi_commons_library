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

/**
 * Configuration class for encryption related beans.
 */
@ConditionalOnProperty(
    prefix = "common-lib.secured",
    name = ["disabled"],
    havingValue = "false",
    matchIfMissing = true,
)
@AutoConfiguration
@EnableConfigurationProperties(EncryptionProperties::class)
class EncryptionConfig {
    /**
     * Returns a KeyProvider implementation based on the given EncryptionProperties.
     *
     * @param properties The EncryptionProperties containing the private and public keys.
     * @return The KeyProvider implementation.
     */
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
    /**
     * Creates a [KeyProvider] for service data encryption using HTTP request headers.
     *
     * @param properties The [EncryptionProperties] containing the necessary configuration properties.
     * @param helper The [HeaderHelper] used to create request headers.
     * @param sensitiveComponent The [SensitiveComponent] used for handling sensitive data.
     * @return A [KeyProvider] instance.
     */
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
    /**
     * Retrieves an instance of the AESServiceDataEncryptionEncrypter.
     * This method is annotated with @ConditionalOnProperty to conditionally create the bean based on the value of the "common-lib.secured.mode" property in the configuration.
     * If the property is not present or its value is "AES", an instance of AesEncrypter will be returned.
     * If the property is present and its value is not "AES", this method will not be invoked and the bean will not be created.
     * This method is also annotated with @ConditionalOnMissingBean to ensure that the bean is only created if there is no existing bean of type Encrypter in the application context
     * .
     *
     * @return An instance of the AESServiceDataEncryptionEncrypter.
     */
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
    /**
     * Retrieves an AES-based decrypter for service data encryption.
     *
     * @return The AES service data decryption implementation.
     */
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
    /**
     * Retrieves an instance of the RSAServiceDataEncryptionEncrypter.
     * This method is annotated with `@ConditionalOnProperty` to ensure that it is only created when the "common-lib.secured.mode" property is set to "RSA" in the configuration.
     * It is also annotated with `@Bean` to indicate that it is a bean that should be managed by the Spring container.
     * If there is already a bean of type `Encrypter` present in the container, this method will not be invoked.
     *
     * @return An instance of the `Encrypter` interface that provides RSA encryption functionality.
     */
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
    /**
     * Retrieves a Decrypter instance that uses RSA encryption algorithm for data decryption.
     *
     * @return The Decrypter instance.
     */
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
    /**
     * Retrieves the SensitiveComponent instance.
     *
     * @return The SensitiveComponent instance.
     */
    @Bean("SensitiveComponent")
    @ConditionalOnMissingBean(SensitiveComponent::class)
    fun getSensitiveComponent(): SensitiveComponent {
        return SensitiveComponent(ObjectMapper())
    }
}

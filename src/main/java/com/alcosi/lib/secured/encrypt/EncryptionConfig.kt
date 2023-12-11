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

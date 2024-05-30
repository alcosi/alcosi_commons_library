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

package com.alcosi.lib.secured.encrypt.key

import com.alcosi.lib.filters.servlet.HeaderHelper
import com.alcosi.lib.logging.JavaLoggingLevel
import com.alcosi.lib.logging.annotations.LogTime
import com.alcosi.lib.secured.encrypt.SensitiveComponent
import org.springframework.http.HttpMethod
import org.springframework.http.RequestEntity
import org.springframework.web.client.RestTemplate
import org.springframework.web.util.UriComponentsBuilder

/**
 * This class provides a key for encryption or decryption in the context of an HTTP request.
 *
 * @param sensitiveComponent An instance of the SensitiveComponent class that handles sensitive data.
 * @param headerHelper An instance of the HeaderHelper class that assists with creating request headers.
 * @param accessKey The access key used for authentication with the key provider.
 * @param baseUrl The base URL of the key provider.
 */
open class HttpRequestKeyProvider(
    protected val sensitiveComponent: SensitiveComponent,
    protected val headerHelper: HeaderHelper,
    protected val accessKey: String,
    protected val baseUrl: String,
) : KeyProvider {
    /**
     * Represents a REST template for making HTTP requests.
     *
     * @property restTemplate The instance of the RestTemplate class used for making HTTP requests.
     */
    protected open val restTemplate = RestTemplate()

    /**
     * Generates a key for encryption or decryption.
     *
     * @param mode The mode in which the key will be used (ENCRYPT or DECRYPT).
     * @return The generated key as a byte array.
     */
    @LogTime(level = JavaLoggingLevel.FINEST)
    override fun key(mode: KeyProvider.MODE): ByteArray {
        val headers = headerHelper.createRequestHeaders()
        headers[HeaderHelper.KEY_PROVIDER_AUTH_HEADER] = accessKey
        val uri =
            UriComponentsBuilder.fromHttpUrl(baseUrl)
                .queryParam("mode", mode.name.lowercase())
                .build()
                .toUri()
        val rq: RequestEntity<Any> = RequestEntity(headers, HttpMethod.GET, uri)
        val rs = restTemplate.exchange(rq, String::class.java).body!!
        return sensitiveComponent.deserialize(rs)!!
    }
}

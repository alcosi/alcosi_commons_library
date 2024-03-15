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
import com.alcosi.lib.secured.encrypt.SensitiveComponent
import org.springframework.http.HttpMethod
import org.springframework.http.RequestEntity
import org.springframework.web.client.RestTemplate
import org.springframework.web.util.UriComponentsBuilder

open class HttpRequestKeyProvider(
    protected val sensitiveComponent: SensitiveComponent,
    protected val headerHelper: HeaderHelper,
    protected val accessKey: String,
    protected val baseUrl: String,
) : KeyProvider {
    protected val restTemplate = RestTemplate()

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

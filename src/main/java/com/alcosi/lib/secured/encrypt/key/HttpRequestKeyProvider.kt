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

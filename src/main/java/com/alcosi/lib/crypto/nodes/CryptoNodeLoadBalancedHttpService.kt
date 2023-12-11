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

package com.alcosi.lib.crypto.nodes

import okhttp3.Headers.Companion.toHeaders
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.RequestBody.Companion.toRequestBody
import okhttp3.ResponseBody
import org.web3j.protocol.exceptions.ClientConnectionException
import org.web3j.protocol.http.HttpService
import java.io.ByteArrayInputStream
import java.io.InputStream

class CryptoNodeLoadBalancedHttpService(
    val chainId: Int,
    val cryptoNodesLoadBalancer: CryptoNodesLoadBalancer,
    val okHttpClientRaw: OkHttpClient,
) : HttpService(okHttpClientRaw) {
    override fun performIO(request: String): InputStream? {
        val requestBody = request.toRequestBody(JSON_MEDIA_TYPE)
        val headersOkHttp = headers.toHeaders()
        val url = cryptoNodesLoadBalancer.getActualUrl(chainId)
        val httpRequest: Request = Request.Builder().url(url.get()).headers(headersOkHttp).post(requestBody).build()
        okHttpClientRaw.newCall(httpRequest).execute().use { response ->
            processHeaders(response.headers)
            val responseBody = response.body
            return if (response.isSuccessful) {
                responseBody?.let { buildInputStream(it) }
            } else {
                val code = response.code
                val text = responseBody?.string() ?: "N/A"
                throw ClientConnectionException(
                    "Invalid response received: $code; $text",
                )
            }
        }
    }

    private fun buildInputStream(responseBody: ResponseBody): InputStream {
        return ByteArrayInputStream(responseBody.bytes())
    }
}

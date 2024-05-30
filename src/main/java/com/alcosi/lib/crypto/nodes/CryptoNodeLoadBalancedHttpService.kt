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

/**
 * A class representing a load-balanced HTTP service for performing IO operations using Crypto Nodes.
 *
 * @param chainId The ID of the Crypto Node chain.
 * @param cryptoNodesLoadBalancer The load balancer for selecting the actual URL of the Crypto Node.
 * @param okHttpClientRaw The raw OkHttpClient instance for making HTTP requests.
 */
open class CryptoNodeLoadBalancedHttpService(
    val chainId: Int,
    val cryptoNodesLoadBalancer: CryptoNodesLoadBalancer,
    val okHttpClientRaw: OkHttpClient,
) : HttpService(okHttpClientRaw) {
    /**
     * Performs an IO operation by making an HTTP POST request with the given request string.
     *
     * @param request The request string to be sent in the HTTP POST request.
     * @return An InputStream containing the response body if the request is successful, else null.
     * @throws ClientConnectionException if the response received is not successful.
     */
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

    /**
     * Builds an input stream from the provided response body.
     *
     * @param responseBody The response body from which to build the input stream.
     * @return The input stream built from the response body.
     */
    protected open fun buildInputStream(responseBody: ResponseBody): InputStream {
        return ByteArrayInputStream(responseBody.bytes())
    }
}

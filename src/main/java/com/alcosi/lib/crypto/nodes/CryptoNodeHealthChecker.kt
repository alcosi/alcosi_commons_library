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

import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.RequestBody
import okhttp3.RequestBody.Companion.toRequestBody
import java.net.URL
import java.util.*
import java.util.logging.Level
import java.util.logging.Logger

open class CryptoNodeHealthChecker(val client: OkHttpClient) {
    val body = createBody()

    @JvmRecord
    data class HeathStatus(val status: Boolean, val timeout: Long) {
        override fun toString(): String {
            return "$status:$timeout ms"
        }
    }

    protected fun createBody(): RequestBody {
        val currentBlockRequest =
            """{"jsonrpc":"2.0","method":"eth_blockNumber","params":[],"id":"ethBlockNumberPing${UUID.randomUUID()}"}"""
        return currentBlockRequest.toRequestBody("application/json".toMediaTypeOrNull())
    }

    fun check(url: URL): HeathStatus {
        val time = System.currentTimeMillis()
        val status = checkStatus(url)
        return HeathStatus(status, System.currentTimeMillis() - time)
    }

    protected fun checkStatus(url: URL): Boolean {
        try {
            val request =
                Request.Builder().url(url)
                    .post(body)
                    .build()
            val call = client.newCall(request)
            val response = call.execute()
            return response.isSuccessful
        } catch (th: Throwable) {
            logger.log(Level.SEVERE, "Error health check $url", th)
            return false
        }
    }

    companion object {
        val logger = Logger.getLogger(this::class.java.name)
    }
}

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

    protected open fun createBody(): RequestBody {
        val currentBlockRequest =
            """{"jsonrpc":"2.0","method":"eth_blockNumber","params":[],"id":"ethBlockNumberPing${UUID.randomUUID()}"}"""
        return currentBlockRequest.toRequestBody("application/json".toMediaTypeOrNull())
    }

    open fun check(url: URL): HeathStatus {
        val time = System.currentTimeMillis()
        val status = checkStatus(url)
        return HeathStatus(status, System.currentTimeMillis() - time)
    }

    protected open fun checkStatus(url: URL): Boolean {
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

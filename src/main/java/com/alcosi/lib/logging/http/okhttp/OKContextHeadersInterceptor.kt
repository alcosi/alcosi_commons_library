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

package com.alcosi.lib.logging.http.okhttp

import com.alcosi.lib.filters.servlet.HeaderHelper
import okhttp3.*
import org.springframework.core.Ordered

/**
 * Interceptor for adding context headers to the OkHttp request.
 *
 * @property headerHelper The HeaderHelper object used to create request headers
 * @property order The order of this interceptor in the chain
 */
open class OKContextHeadersInterceptor(protected val headerHelper: HeaderHelper, private val order: Int) : Interceptor, Ordered {
    /**
     * Intercepts the OkHttp chain and adds request headers using the provided HeaderHelper object.
     *
     * @param chain The*/
    override fun intercept(chain: Interceptor.Chain): Response {
        return chain.proceed(addRequestHeaders(chain))
    }

    /**
     * Adds missing request headers to the given Interceptor Chain's request.
     *
     * @param chain The Interceptor Chain.
     * @return The modified Request with added headers.
     */
    protected open fun addRequestHeaders(chain: Interceptor.Chain): Request {
        val httpRequestBuilder = chain.request().newBuilder()
        headerHelper.createRequestHeadersMap()
            .filter { chain.request().headers[it.key] == null }
            .forEach {
                httpRequestBuilder.addHeader(it.key, it.value)
            }
        val httpRequest = httpRequestBuilder.build()
        return httpRequest
    }

    /**
     * Returns the order of this interceptor in the chain.
     *
     * @return The order of this interceptor
     */
    override fun getOrder(): Int {
        return order
    }
}

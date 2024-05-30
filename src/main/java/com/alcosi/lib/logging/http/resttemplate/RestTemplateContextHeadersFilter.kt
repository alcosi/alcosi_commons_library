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

package com.alcosi.lib.logging.http.resttemplate

import com.alcosi.lib.filters.servlet.HeaderHelper
import org.springframework.core.Ordered
import org.springframework.http.HttpRequest
import org.springframework.http.client.ClientHttpRequestExecution
import org.springframework.http.client.ClientHttpRequestInterceptor
import org.springframework.http.client.ClientHttpResponse

/**
 * A client HTTP request interceptor that adds context headers to the request.
 *
 * @property headerHelper The HeaderHelper instance used by the filter.
 * @property order The order in which the filter should be applied. Default is 0.
 */
open class RestTemplateContextHeadersFilter(
    val headerHelper: HeaderHelper,
    private val order: Int = 0,
) : ClientHttpRequestInterceptor, Ordered {
    /**
     * Intercepts a client HTTP request and adds context headers to the request.
     *
     * @param request The original HTTP request.
     * @param body The request body as a byte array.
     * @param execution The `ClientHttpRequestExecution` object used to execute the request.
     * @return The response to the intercepted request.
     */
    override fun intercept(
        request: HttpRequest,
        body: ByteArray,
        execution: ClientHttpRequestExecution,
    ): ClientHttpResponse {
        return execution.execute(addHeaders(request), body)
    }

    /**
     * Adds headers to the given HttpRequest.
     *
     * @param request The HttpRequest to add headers to.
     * @return The modified HttpRequest with added headers.
     */
    protected open fun addHeaders(request: HttpRequest): HttpRequest {
        headerHelper.createRequestHeadersMap()
            .forEach {
                request.headers[it.key] = it.value
            }
        return request
    }

    /**
     * Returns the order in which the filter should be applied.
     *
     * @return The order of the filter.
     */
    override fun getOrder(): Int {
        return order
    }
}

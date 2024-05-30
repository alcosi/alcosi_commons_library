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

package com.alcosi.lib.filters.servlet.cors

import jakarta.servlet.FilterChain
import jakarta.servlet.http.HttpServletRequest
import jakarta.servlet.http.HttpServletResponse
import org.springframework.http.HttpHeaders
import org.springframework.http.HttpMethod
import org.springframework.http.MediaType
import org.springframework.web.filter.OncePerRequestFilter

/**
 * The CorsFilter class is responsible for handling Cross-Origin Resource Sharing (CORS) requests.
 * It extends the OncePerRequestFilter class, which ensures that the filter's doFilterInternal method
 * is executed only once per request.
 *
 * The filter sets the necessary response headers for CORS, including the Access-Control-Allow-Origin,
 * Access-Control-Allow-Methods, Access-Control-Allow-Headers, and Access-Control-Allow-Credentials headers.
 *
 * If the request method is not OPTIONS, the filter continues processing the request by invoking the
 * next filter in the filter chain. Otherwise, it returns a 200 response with an empty JSON body.
 */
open class CorsFilter : OncePerRequestFilter() {
    /**
     * This method is responsible for handling CORS requests. It sets the necessary response
     * headers for CORS, including the Access-Control-Allow-Origin, Access-Control-Allow-Methods,
     * Access-Control-Allow-Headers, and Access-Control-Allow-Credentials headers. If the request
     * method is not OPTIONS, the method continues processing the request by invoking the next
     * filter in the filter chain. Otherwise, it returns a 200 response with an empty JSON body.
     *
     * @param request The HTTP servlet request.
     * @param response The HTTP servlet response.
     * @param filterChain The filter chain to continue processing the request.
     */
    override fun doFilterInternal(
        request: HttpServletRequest,
        response: HttpServletResponse,
        filterChain: FilterChain,
    ) {
        val responseHeaders = HttpHeaders()
        responseHeaders.accessControlAllowOrigin = "*"
        responseHeaders.accessControlAllowMethods = HttpMethod.values().asList()
        responseHeaders.accessControlAllowHeaders = listOf("*")
        responseHeaders.accessControlAllowCredentials = true
        responseHeaders.forEach {
            it.value.forEach { v -> response.addHeader(it.key, v) }
        }
        val haveToProcess = request.method.uppercase() != "OPTIONS"
        if (haveToProcess) {
            filterChain.doFilter(request, response)
        } else {
            response.status = 200
            response.outputStream.write("{}".toByteArray(Charsets.UTF_8))
            response.contentType = MediaType.APPLICATION_JSON_VALUE
        }
    }
}

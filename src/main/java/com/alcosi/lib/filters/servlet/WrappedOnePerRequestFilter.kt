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

package com.alcosi.lib.filters.servlet

import com.alcosi.lib.filters.servlet.cache.CachingRequestWrapper
import jakarta.servlet.FilterChain
import jakarta.servlet.http.HttpServletRequest
import jakarta.servlet.http.HttpServletResponse
import org.springframework.web.filter.OncePerRequestFilter
import org.springframework.web.util.ContentCachingResponseWrapper

/**
 * This abstract class represents a filter that ensures that only one instance of the filter is executed per request.
 * It extends the OncePerRequestFilter class.
 * The filter processes the HttpServletRequest and HttpServletResponse with the help of two abstract methods:
 *     - doFilterWrapped: This method*/
abstract class WrappedOnePerRequestFilter(
    val maxBodySize: Int,
) : OncePerRequestFilter() {
    /**
     * Apply the filter to the incoming HTTP request and response.
     *
     * @param request The incoming HTTP request.
     * @param response The HTTP response.
     * @param filterChain The filter chain to continue processing the request.
     */
    override fun doFilterInternal(
        request: HttpServletRequest,
        response: HttpServletResponse,
        filterChain: FilterChain,
    ) {
        doFilterWrapped(wrapRequest(request), wrapResponse(response), filterChain)
    }

    /**
     * Performs the actual filtering of the request and response.
     *
     * @param request The wrapped version of the HttpServletRequest.
     * @param response The wrapped version of the HttpServletResponse.
     * @param filterChain The FilterChain object that manages the filter chain.
     */
    protected abstract fun doFilterWrapped(
        request: CachingRequestWrapper,
        response: ContentCachingResponseWrapper,
        filterChain: FilterChain,
    )

    /**
     * Wraps the given HttpServletRequest object with a CachingRequestWrapper.
     *
     * @param request the HttpServletRequest object to be wrapped
     * @return a CachingRequestWrapper that wraps the given HttpServletRequest object
     */
    protected open fun wrapRequest(request: HttpServletRequest): CachingRequestWrapper =
        if (request is CachingRequestWrapper) {
            request
        } else {
            CachingRequestWrapper(maxBodySize, request)
        }

    /**
     * Wraps the given HttpServletResponse with a ContentCachingResponseWrapper.
     *
     * @param response The HttpServletResponse to be wrapped.
     * @return The ContentCachingResponseWrapper that wraps the given response.
     */
    protected open fun wrapResponse(response: HttpServletResponse): ContentCachingResponseWrapper =
        if (response is ContentCachingResponseWrapper) {
            response
        } else {
            ContentCachingResponseWrapper(response)
        }
}

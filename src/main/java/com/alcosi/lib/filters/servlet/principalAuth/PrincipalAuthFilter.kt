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

package com.alcosi.lib.filters.servlet.principalAuth

import com.alcosi.lib.filters.servlet.HeaderHelper
import com.alcosi.lib.filters.servlet.HeaderHelper.Companion.ORIGINAL_AUTHORISATION
import com.alcosi.lib.filters.servlet.ThreadContext
import com.alcosi.lib.filters.servlet.ThreadContext.Companion.REQUEST_ORIGINAL_AUTHORISATION_TOKEN
import com.alcosi.lib.filters.servlet.WrappedOnePerRequestFilter
import com.alcosi.lib.filters.servlet.cache.CachingRequestWrapper
import com.alcosi.lib.objectMapper.mapOne
import com.alcosi.lib.secured.encrypt.SensitiveComponent
import com.alcosi.lib.security.AccountDetails
import com.alcosi.lib.security.PrincipalDetails
import com.alcosi.lib.security.UserDetails
import com.fasterxml.jackson.databind.ObjectMapper
import jakarta.servlet.FilterChain
import org.springframework.web.util.ContentCachingResponseWrapper
import java.nio.charset.Charset

/**
 * The PrincipalAuthFilter class is a filter used for authentication and setting the authentication principal
 * in the thread context for each request. It extends the WrappedOnePerRequestFilter class which ensures that
 * each request is processed only once by the filter.
 *
 * @param mappingHelper The ObjectMapper used for mapping JSON strings to objects.
 * @param threadContext The ThreadContext object used for managing thread-local data.
 * @param sensitiveComponent The SensitiveComponent used for deserializing sensitive data.
 */
open class PrincipalAuthFilter(
    protected open val mappingHelper: ObjectMapper,
    protected open val threadContext: ThreadContext,
    protected open val sensitiveComponent: SensitiveComponent,
) : WrappedOnePerRequestFilter(Int.MAX_VALUE) {
    /**
     * Executes the filter logic by setting the authentication principal in the thread context and
     * calling the next filter in the filter chain.
     *
     * @param request The CachingRequestWrapper representing the incoming request.
     * @param response The ContentCachingResponseWrapper representing the response.
     * @param filterChain The FilterChain to invoke the next filter.
     */
    override fun doFilterWrapped(
        request: CachingRequestWrapper,
        response: ContentCachingResponseWrapper,
        filterChain: FilterChain,
    ) {
        try {
            try {
                val principal = getPrincipalOrNull(request)
                threadContext.setAuthPrincipal(principal)
                request.setAttribute(ThreadContext.AUTH_PRINCIPAL, principal)
                val originalToken = request.getHeader(ORIGINAL_AUTHORISATION)?.let { sensitiveComponent.deserialize(it)?.toString(Charset.defaultCharset()) }
                originalToken?.let { request.setAttribute(REQUEST_ORIGINAL_AUTHORISATION_TOKEN, it) }
                originalToken?.let { threadContext.set(REQUEST_ORIGINAL_AUTHORISATION_TOKEN, it) }
            } catch (t: Throwable) {
                logger.error("Error during auth", t)
            }
            filterChain.doFilter(request, response)
        } finally {
            threadContext.clear()
        }
    }

    /**
     * Returns the PrincipalDetails representing the principal for the given request.
     *
     * If the request contains the header "USER_DETAILS", this method will try to map the value of the header to an instance of UserDetails using the mappingHelper. If the mapping
     *  is successful, the UserDetails instance is returned.
     * If the user header is not present or the mapping fails, this method will try to map the value of the header "ACCOUNT_DETAILS" to an instance of AccountDetails using the mapping
     * Helper. If the mapping is successful, the AccountDetails instance is returned.
     * If both headers are missing or the mapping fails for both, null is returned.
     *
     * @param request The CachingRequestWrapper representing the incoming request.
     * @return The PrincipalDetails representing the principal for the given request, or null if no valid principal is found.
     */
    protected open fun getPrincipalOrNull(request: CachingRequestWrapper): PrincipalDetails? {
        val user = request.getHeader(HeaderHelper.USER_DETAILS)?.let { mappingHelper.mapOne(it, UserDetails::class.java) }
        return if (user != null) {
            user
        } else {
            request.getHeader(HeaderHelper.ACCOUNT_DETAILS)?.let { mappingHelper.mapOne(it, AccountDetails::class.java) }
        }
    }
}

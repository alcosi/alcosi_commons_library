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
import com.alcosi.lib.objectMapper.MappingHelper
import com.alcosi.lib.secured.encrypt.SensitiveComponent
import com.alcosi.lib.security.AccountDetails
import com.alcosi.lib.security.PrincipalDetails
import com.alcosi.lib.security.UserDetails
import jakarta.servlet.FilterChain
import org.springframework.web.util.ContentCachingResponseWrapper
import java.nio.charset.Charset

open class PrincipalAuthFilter(
    protected open val mappingHelper: MappingHelper,
    protected open val threadContext: ThreadContext,
    protected open val sensitiveComponent: SensitiveComponent,
) : WrappedOnePerRequestFilter(Int.MAX_VALUE) {
    override fun doFilterWrapped(
        request: CachingRequestWrapper,
        response: ContentCachingResponseWrapper,
        filterChain: FilterChain,
    ) {
        try {
            try {
                val principal = getPrincipal(request)
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

    protected open fun getPrincipal(request: CachingRequestWrapper): PrincipalDetails? {
        val user = request.getHeader(HeaderHelper.USER_DETAILS)?.let { mappingHelper.mapOne(it, UserDetails::class.java) }
        if (user != null) {
            return user
        } else {
            return request.getHeader(HeaderHelper.ACCOUNT_DETAILS)?.let { mappingHelper.mapOne(it, AccountDetails::class.java) }
        }
    }
}

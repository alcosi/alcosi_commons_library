package com.alcosi.lib.filters.servlet.principalAuth

import com.alcosi.lib.filters.servlet.HeaderHelper
import com.alcosi.lib.filters.servlet.HeaderHelper.Companion.ORIGINAL_AUTHORISATION
import com.alcosi.lib.filters.servlet.ThreadContext
import com.alcosi.lib.filters.servlet.ThreadContext.Companion.REQUEST_ORIGINAL_AUTHORISATION_TOKEN
import com.alcosi.lib.filters.servlet.WrappedOnePerRequestFilter
import com.alcosi.lib.filters.servlet.cache.CachingRequestWrapper
import com.alcosi.lib.objectMapper.MappingHelper
import com.alcosi.lib.objectMapper.mapOne
import com.alcosi.lib.objectMapper.mapOneNode
import com.alcosi.lib.security.AccountDetails
import com.alcosi.lib.security.ClientAccountDetails
import com.alcosi.lib.security.PrincipalDetails
import com.alcosi.lib.security.UserDetails
import com.fasterxml.jackson.databind.JsonNode
import jakarta.servlet.FilterChain
import org.springframework.web.util.ContentCachingResponseWrapper

open class PrincipalAuthFilter(val mappingHelper: MappingHelper, val threadContext: ThreadContext) : WrappedOnePerRequestFilter(Int.MAX_VALUE) {
    override fun doFilterWrapped(
        request: CachingRequestWrapper,
        response: ContentCachingResponseWrapper,
        filterChain: FilterChain,
    ) {
        try {
            val principal = getPrincipal(request)
            principal?.let { threadContext.setAuthPrincipal(it) }
            principal?.let { request.setAttribute(ThreadContext.AUTH_PRINCIPAL, it) }
            val originalToken = request.getHeader(ORIGINAL_AUTHORISATION)
            originalToken?.let { request.setAttribute(REQUEST_ORIGINAL_AUTHORISATION_TOKEN, it) }
            originalToken?.let { threadContext.set(REQUEST_ORIGINAL_AUTHORISATION_TOKEN, it) }
        } catch (t: Throwable) {
            logger.error("Error during auth", t)
        }
        filterChain.doFilter(request, response)
    }

    protected open fun getPrincipal(request: CachingRequestWrapper): PrincipalDetails? {
        val user = request.getHeader(HeaderHelper.USER_DETAILS)?.let { mappingHelper.mapOne(it, UserDetails::class.java) }
        if (user != null) {
            return user
        } else {
            return getAccount(request)
        }
    }

    protected open fun getAccount(request: CachingRequestWrapper): AccountDetails? {
        val node = request.getHeader(HeaderHelper.ACCOUNT_DETAILS)?.let { mappingHelper.mapOne<JsonNode>(it) }
        return if (node == null) {
            null
        } else if (node.hasNonNull("clientId")) {
            mappingHelper.mapOneNode<ClientAccountDetails>(node)
        } else {
            mappingHelper.mapOneNode<AccountDetails>(node)
        }
    }
}

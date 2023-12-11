package com.alcosi.lib.filters.servlet.principalAuth

import com.alcosi.lib.filters.servlet.HeaderHelper
import com.alcosi.lib.filters.servlet.ThreadContext
import com.alcosi.lib.filters.servlet.WrappedOnePerRequestFilter
import com.alcosi.lib.filters.servlet.cache.CachingRequestWrapper
import com.alcosi.lib.objectMapper.MappingHelper
import com.alcosi.lib.security.AccountDetails
import com.alcosi.lib.security.ClientAccountDetails
import com.alcosi.lib.security.PrincipalDetails
import com.alcosi.lib.security.UserDetails
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
            principal?.let { request.setAttribute(HeaderHelper.AUTH_PRINCIPAL, it) }
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
        val account = request.getHeader(HeaderHelper.ACCOUNT_DETAILS)?.let { mappingHelper.mapOne(it, ClientAccountDetails::class.java) }
        return if (account == null) {
            null
        } else {
            if (account.clientId == null) {
                AccountDetails(account.id, account.authorities)
            } else {
                account
            }
        }
    }
}

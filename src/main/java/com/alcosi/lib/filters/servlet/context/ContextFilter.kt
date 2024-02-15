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

package com.alcosi.lib.filters.servlet.context

import com.alcosi.lib.filters.servlet.ThreadContext
import com.fasterxml.jackson.databind.ObjectMapper
import jakarta.servlet.FilterChain
import jakarta.servlet.http.HttpServletRequest
import jakarta.servlet.http.HttpServletResponse
import org.springframework.web.filter.OncePerRequestFilter
import java.util.logging.Logger
import kotlin.reflect.KClass

open class ContextFilter(
    protected val threadContext: ThreadContext,
    protected val objectMapper: ObjectMapper,
    protected val contextHeaders: List<String>,
    protected val jsonHeaders: List<JsonHeader>,
    protected val headersConfig: ContextFilterProperties.Headers,
) : OncePerRequestFilter() {
    data class JsonHeader(val header: String, val clazz: KClass<*>, val threadContextName: String)

    override fun doFilterInternal(
        request: HttpServletRequest,
        response: HttpServletResponse,
        filterChain: FilterChain,
    ) {
        parseHeaders(request)
        filterChain.doFilter(request, response)
        setResponseHeaders(request, response)
    }

    private fun setResponseHeaders(
        request: HttpServletRequest,
        response: HttpServletResponse,
    ) {
        val values = contextHeaders.map { h -> h to request.getHeader(h) }.filter { it.second != null }
        values
            .filter { !response.containsHeader(it.first) }
            .forEach { response.setHeader(it.first, it.second) }
    }

    protected open fun parseHeaders(request: HttpServletRequest) {
        setJson(request)
        setPlain(request)
        setRequestContext(request)
        threadContext.set("HTTP_REQUEST", request)
    }

    protected open fun setPlain(request: HttpServletRequest) {
        val values = contextHeaders.map { h -> h to request.getHeader(h) }.filter { it.second != null }
        values.forEach { threadContext.set(it.first, it.second) }
        values.forEach { request.setAttribute(it.first, it.second) }
    }

    protected open fun setRequestContext(request: HttpServletRequest) {
        request.getHeader(headersConfig.userAgent)?.let { threadContext.set(ThreadContext.REQUEST_ORIGINAL_USER_AGENT, it) }
        (request.getHeader(headersConfig.ip) ?: request.remoteAddr)?.let { threadContext.set(ThreadContext.REQUEST_ORIGINAL_IP, it) }
        request.getHeader(headersConfig.platform)?.let { threadContext.set(ThreadContext.REQUEST_PLATFORM, it) }
    }

    protected open fun setJson(request: HttpServletRequest) {
        val values = jsonHeaders.filter { j -> request.getHeader(j.header) != null }
        values
            .forEach { j ->
                try {
                    val readValue = objectMapper.readValue(request.getHeader(j.header), j.clazz.java)
                    threadContext.set(j.threadContextName, readValue)
                    request.setAttribute(j.threadContextName, readValue)
                } catch (t: Throwable) {
                    logger.error("Error mapping thread value ${j.header}")
                }
            }
    }

    companion object {
        val logger = Logger.getLogger(this.javaClass.name)
    }
}

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

package com.alcosi.lib.filters.servlet.context

import com.alcosi.lib.filters.servlet.ThreadContext
import com.alcosi.lib.objectMapper.MappingHelper
import jakarta.servlet.FilterChain
import jakarta.servlet.http.HttpServletRequest
import jakarta.servlet.http.HttpServletResponse
import org.springframework.web.filter.OncePerRequestFilter
import java.util.logging.Logger
import kotlin.reflect.KClass

open class ContextFilter(
    protected val threadContext: ThreadContext,
    protected val mappingHelper: MappingHelper,
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
        try {
            parseHeaders(request)
            filterChain.doFilter(request, response)
            setResponseHeaders(request, response)
        } finally {
            threadContext.clear()
        }
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
                    val obj = mapJsonObject(j.clazz.java, request, j)
                    threadContext.set(j.threadContextName, obj)
                    request.setAttribute(j.threadContextName, obj)
                } catch (t: Throwable) {
                    logger.error("Error mapping thread value ${j.header}")
                }
            }
    }

    private fun mapJsonObject(
        javaClass: Class<out Any>,
        request: HttpServletRequest,
        j: JsonHeader,
    ): Any {
        return mappingHelper.mapOne(request.getHeader(j.header), javaClass)!!
    }

    companion object {
        val logger = Logger.getLogger(this.javaClass.name)
    }
}

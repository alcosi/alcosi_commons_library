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

import com.alcosi.lib.filters.servlet.HeaderHelper
import com.alcosi.lib.filters.servlet.ThreadContext
import com.alcosi.lib.objectMapper.mapOne
import com.fasterxml.jackson.databind.ObjectMapper
import jakarta.servlet.FilterChain
import jakarta.servlet.http.HttpServletRequest
import jakarta.servlet.http.HttpServletResponse
import org.springframework.web.filter.OncePerRequestFilter
import java.util.logging.Logger
import kotlin.reflect.KClass

/**
 * The ContextFilter class is a filter class that performs operations on the request and response
 * headers and sets context information using the ThreadContext class.
 *
 * @param threadContext The ThreadContext object used to store and retrieve thread-local data.
 * @param mappingHelper The ObjectMapper object used to map JSON values to Java objects.
 * @param contextHeaders The list of request headers used to set context values in the ThreadContext.
 * @param jsonHeaders The list of JSON headers used to map JSON values to Java objects and set them in the ThreadContext.
 * @param headersConfig The configuration properties for the request headers.
 */
open class ContextFilter(
    protected val threadContext: ThreadContext,
    protected val mappingHelper: ObjectMapper,
    protected val contextHeaders: List<String>,
    protected val jsonHeaders: List<JsonHeader>,
    protected val headersConfig: ContextFilterProperties.Headers,
) : OncePerRequestFilter() {
    data class JsonHeader(val header: String, val clazz: KClass<*>, val threadContextName: String)

    /**
     * Applies the filtering logic to the request and response.
     *
     * @param request The HttpServletRequest object representing the incoming request.
     * @param response The HttpServletResponse object representing the response.
     * @param filterChain The FilterChain object to invoke the next filter in the chain.
     */
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

    /**
     * Sets the response headers based on the context headers present in the request.
     *
     * @param request The HttpServletRequest object representing the incoming request.
     * @param response The HttpServletResponse object representing the response.
     */
    protected open fun setResponseHeaders(
        request: HttpServletRequest,
        response: HttpServletResponse,
    ) {
        val values = contextHeaders.map { h -> h to request.getHeader(h) }.filter { it.second != null }
        values
            .filter { !response.containsHeader(it.first) }
            .forEach { response.setHeader(it.first, it.second) }
    }

    /**
     * Parses the headers from the HttpServletRequest object and sets them in the appropriate context.
     *
     * @param request The HttpServletRequest object representing the incoming request.
     */
    protected open fun parseHeaders(request: HttpServletRequest) {
        setJson(request)
        setPlain(request)
        setRequestContext(request)
        threadContext.set("HTTP_REQUEST", request)
    }

    /**
     * Sets the plain context headers based on the values present in the HttpServletRequest object.
     *
     * @param request The HttpServletRequest object representing the incoming request.
     */
    protected open fun setPlain(request: HttpServletRequest) {
        val values = contextHeaders.map { h -> h to request.getHeader(h) }.filter { it.second != null }
        values.forEach { threadContext.set(it.first, it.second) }
        values.forEach { request.setAttribute(it.first, it.second) }
    }

    /**
     * Sets the request context based on the headers in the provided HttpServletRequest object.
     *
     * @param request The HttpServletRequest object representing the incoming request.
     */
    protected open fun setRequestContext(request: HttpServletRequest) {
        request.getHeader(HeaderHelper.RQ_ID)?.let { threadContext.set(ThreadContext.RQ_ID, it) }
        request.getHeader(headersConfig.userAgent)?.let { threadContext.set(ThreadContext.REQUEST_ORIGINAL_USER_AGENT, it) }
        request.getHeader(headersConfig.userAgent)?.let { threadContext.set(ThreadContext.REQUEST_ORIGINAL_USER_AGENT, it) }
        (request.getHeader(headersConfig.ip) ?: request.remoteAddr)?.let { threadContext.set(ThreadContext.REQUEST_ORIGINAL_IP, it) }
        request.getHeader(headersConfig.platform)?.let { threadContext.set(ThreadContext.REQUEST_PLATFORM, it) }
    }

    /**
     * Sets the JSON headers from the HttpServletRequest object and maps them*/
    protected open fun setJson(request: HttpServletRequest) {
        val values = jsonHeaders.filter { j -> request.getHeader(j.header) != null }
        values
            .forEach { j ->
                try {
                    val obj = mapJsonObject(j.clazz.java, request, j)
                    threadContext.set(j.threadContextName, obj)
                    request.setAttribute(j.threadContextName, obj)
                } catch (t: Throwable) {
                    super.logger.error("Error mapping thread value ${j.header}")
                }
            }
    }

    /**
     * Maps a JSON object to an instance of the specified class.
     *
     * @param javaClass The class of the object to be mapped.
     * @param request The HttpServletRequest object representing the incoming request.
     * @param j The JsonHeader object containing the header information.
     * @return An instance of the specified class representing the JSON object.
     * @throws Exception if there is an error while mapping the JSON object.
     */
    protected open fun mapJsonObject(
        javaClass: Class<out Any>,
        request: HttpServletRequest,
        j: JsonHeader,
    ): Any {
        return mappingHelper.mapOne(request.getHeader(j.header), javaClass)!!
    }

    /**
     * The `Companion` object is a companion object of the `ContextFilter` class.
     * It provides a logger property that can be accessed without an instance of the class.
     */
    companion object {
        val logger = Logger.getLogger(this.javaClass.name)
    }
}

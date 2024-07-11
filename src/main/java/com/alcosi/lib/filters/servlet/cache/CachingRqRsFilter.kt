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

package com.alcosi.lib.filters.servlet.cache

import io.github.breninsul.javatimerscheduler.registry.SchedulerType
import io.github.breninsul.javatimerscheduler.registry.TaskSchedulerRegistry
import jakarta.servlet.FilterChain
import jakarta.servlet.http.HttpServletRequest
import jakarta.servlet.http.HttpServletResponse
import org.springframework.lang.Nullable
import org.springframework.web.filter.OncePerRequestFilter
import org.springframework.web.util.ContentCachingResponseWrapper
import java.time.Duration
import java.time.LocalDateTime
import java.util.*
import java.util.function.Consumer
import java.util.logging.Level
import java.util.stream.Collectors

/**
 * A filter that performs caching of request and response objects.
 *
 * @constructor Creates a CachingRqRsFilter with the given refresh URI,
 *     maximum body size, and clear delay.
 * @property refreshUri The URI that triggers the cache to be cleared.
 * @property maxBodySize The maximum size of the request body.
 * @property clearDelay The delay duration for clearing the cache.
 */
open class CachingRqRsFilter(
    val refreshUri: String,
    maxBodySize: Int,
    val clearDelay: Duration,
) : OncePerRequestFilter() {
    init {
        TaskSchedulerRegistry.registerTypeTask(SchedulerType.VIRTUAL_WAIT, "ClearRqRsCache", clearDelay, clearDelay, this::class, Level.FINEST) { clearCache() }
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

    /**
     * Represents an object stored in the cache.
     *
     * @property body The cached object data as a byte array.
     * @property headers The headers associated with the cached object, stored
     *     as key-value pairs in a map.
     * @property rsCode The HTTP response code associated with the cached
     *     object.
     * @property lifetime The expiration time of the cached object, represented
     *     by a LocalDateTime instance.
     */

    data class CacheObject(
        val body: ByteArray,
        val headers: Map<String, List<String>>,
        val rsCode: Int,
        val lifetime: LocalDateTime,
    )

    /**
     * Retrieves the name of the filter.
     *
     * @return The name of the filter, or null if not set.
     */
    @Nullable
    override fun getFilterName(): String? = "Caching"

    override fun doFilterInternal(
        request: HttpServletRequest,
        response: HttpServletResponse,
        filterChain: FilterChain,
    ) {
        val uri = getUri(request)
        if (uri == refreshUri) {
            cache.clear()
        }
        if (cache.containsKey(uri)) {
            val cacheObject = cache[uri]!!
            response.status = cacheObject.rsCode
            response.setHeaders(cacheObject)
            val outputStream = response.outputStream
            outputStream.write(cacheObject.body)
            outputStream.flush()
            logger.info("Request $uri cache used ")
        } else {
            val wrappedResponse = wrapResponse(response)
            filterChain.doFilter(request, wrappedResponse)
            val cacheable = (true == request.getAttribute(CACHE_REQUEST_ATTRIBUTE))
            if (cacheable) {
                val headers: HashMap<String, List<String>> = HashMap()
                response.headerNames
                    .forEach { hn: String ->
                        headers[hn] = LinkedList(response.getHeaders(hn))
                    }
                val body = wrappedResponse.contentAsByteArray
                wrappedResponse.copyBodyToResponse()
                val lifetime = request.getAttribute(CACHE_REQUEST_LIFETIME_ATTRIBUTE) as Duration
                val validTill = LocalDateTime.now().plus(lifetime)
                cache[uri] = CacheObject(body, headers, wrappedResponse.status, validTill)
                logger.info("Request $uri has been cached till $validTill")
            }
        }
    }

    /** Cache variable used to store cache objects. Cache objects */
    protected val cache: MutableMap<String, CacheObject> = HashMap()

    /**
     * Returns the URI of the HTTP request.
     *
     * @param request The HttpServletRequest object representing the HTTP
     *     request.
     * @return The URI of the request, including the method, request URI, and
     *     query string if present.
     */
    protected open fun getUri(request: HttpServletRequest): String {
        val queryString = if (request.queryString == null) "" else "?" + request.queryString
        return request.method + request.requestURI + queryString
    }

    /**
     *
     */
    protected open fun clearCache() {
        val now = LocalDateTime.now()
        cache
            .entries
            .stream()
            .filter { (_, value): Map.Entry<String, CacheObject> -> value.lifetime.isBefore(now) }
            .map { (key): Map.Entry<String, CacheObject> -> key }
            .collect(Collectors.toSet())
            .forEach(Consumer { key: String -> cache.remove(key) })
    }

    /**
     * Sets the headers of the response based on the provided CacheObject.
     *
     * @param response The ContentCachingResponseWrapper representing the
     *     response.
     * @param cacheObject The CacheObject containing the headers to be set.
     */
    protected open fun HttpServletResponse.setHeaders(cacheObject: CacheObject?) {
        cacheObject!!
            .headers
            .filter { it.key != null }
            .forEach { (key: String, value: List<String?>) ->
                value
                    .forEach { v: String? ->
                        if (v != null) {
                            this.setHeader(key, v)
                        }
                    }
            }
    }

    /**
     * The `Companion` class represents a companion object with utility methods
     * and constants for caching requests and responses.
     *
     * @property CACHE_REQUEST_ATTRIBUTE_NAME The attribute name for caching
     *     requests.
     * @property CACHE_REQUEST_LIFETIME_ATTRIBUTE_NAME The attribute name for
     *     caching request lifetime.
     */
    companion object {
        const val CACHE_REQUEST_ATTRIBUTE = "CacheRequest"
        const val CACHE_REQUEST_LIFETIME_ATTRIBUTE = "CacheRequestLifetime"
    }
}

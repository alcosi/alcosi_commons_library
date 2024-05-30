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

import com.alcosi.lib.filters.servlet.WrappedOnePerRequestFilter
import io.github.breninsul.javatimerscheduler.registry.SchedulerType
import io.github.breninsul.javatimerscheduler.registry.TaskSchedulerRegistry
import jakarta.servlet.FilterChain
import jakarta.servlet.http.HttpServletRequest
import org.springframework.lang.Nullable
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
open class CachingRqRsFilter(val refreshUri: String, maxBodySize: Int, val clearDelay: Duration) : WrappedOnePerRequestFilter(maxBodySize) {
    init {
        TaskSchedulerRegistry.registerTypeTask(SchedulerType.VIRTUAL_WAIT, "ClearRqRsCache", clearDelay, clearDelay, this::class, Level.FINEST) { clearCache() }
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
    @JvmRecord
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
    override fun getFilterName(): String? {
        return "Caching"
    }

    /** Cache variable used to store cache objects. Cache objects */
    protected val cache: MutableMap<String, CacheObject> = HashMap()

    /**
     * This method filters the HTTP request and response using the provided
     * wrappers and filter chain. It checks if the specified URI is present
     * in the cache. If found, it retrieves the cached response and sets the
     * appropriate headers and body to the response wrapper. If not found, the
     * filter chain is invoked to process the request and response. If the
     * request is marked as cacheable, the response body, headers, HTTP status,
     * and valid till time are stored in the cache for future use.
     *
     * @param request The caching request wrapper.
     * @param response The content caching response wrapper.
     * @param filterChain The filter chain to invoke if the URI is not found in
     *     the cache.
     */
    override fun doFilterWrapped(
        request: CachingRequestWrapper,
        response: ContentCachingResponseWrapper,
        filterChain: FilterChain,
    ) {
        val uri = getUri(request)
        if (uri == refreshUri) {
            cache.clear()
        }
        if (cache.containsKey(uri)) {
            val cacheObject = cache[uri]!!
            response.status = cacheObject.rsCode
            setHeaders(response, cacheObject)
            val outputStream = response.response.outputStream
            outputStream.write(cacheObject.body)
            outputStream.flush()
            logger.info("Request $uri cache used ")
        } else {
            filterChain.doFilter(request, response)
            val cacheable = (true == request.getAttribute(CACHE_REQUEST_ATTRIBUTE_NAME))
            if (cacheable) {
                val headers: HashMap<String, List<String>> = HashMap()
                response.headerNames
                    .forEach { hn: String ->
                        headers[hn] = LinkedList(response.getHeaders(hn))
                    }
                val body = response.contentAsByteArray
                response.copyBodyToResponse()
                val lifetime =
                    request.getAttribute(CACHE_REQUEST_LIFETIME_ATTRIBUTE_NAME) as Duration
                val validTill = LocalDateTime.now().plus(lifetime)
                cache[uri] = CacheObject(body, headers, response.status, validTill)
                logger.info("Request $uri has been cached till $validTill")
            }
        }
    }

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
     * The `Companion` class represents a companion object with utility methods
     * and constants for caching requests and responses.
     *
     * @property CACHE_REQUEST_ATTRIBUTE_NAME The attribute name for caching
     *     requests.
     * @property CACHE_REQUEST_LIFETIME_ATTRIBUTE_NAME The attribute name for
     *     caching request lifetime.
     */
    companion object {
        const val CACHE_REQUEST_ATTRIBUTE_NAME = "CacheRequest"
        const val CACHE_REQUEST_LIFETIME_ATTRIBUTE_NAME = "CacheRequestLifetime"

        /**
         * Sets the headers of the response based on the provided CacheObject.
         *
         * @param response The ContentCachingResponseWrapper representing the
         *     response.
         * @param cacheObject The CacheObject containing the headers to be set.
         */
        protected fun setHeaders(
            response: ContentCachingResponseWrapper,
            cacheObject: CacheObject?,
        ) {
            cacheObject!!
                .headers
                .filter { it.key != null }
                .forEach { (key: String, value: List<String?>) ->
                    value
                        .forEach { v: String? ->
                            if (v != null) {
                                response.setHeader(key, v)
                            }
                        }
                }
        }
    }
}

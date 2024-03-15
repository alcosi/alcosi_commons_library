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

import com.alcosi.lib.executors.SchedulerTimer
import com.alcosi.lib.filters.servlet.WrappedOnePerRequestFilter
import jakarta.servlet.FilterChain
import jakarta.servlet.http.HttpServletRequest
import org.springframework.lang.Nullable
import org.springframework.web.util.ContentCachingResponseWrapper
import java.time.Duration
import java.time.LocalDateTime
import java.util.*
import java.util.function.BiConsumer
import java.util.function.Consumer
import java.util.stream.Collectors

open class CachingRqRsFilter(val refreshUri: String, maxBodySize: Int, val clearDelay: Duration) : WrappedOnePerRequestFilter(maxBodySize) {
    @JvmRecord
    data class CacheObject(
        val body: ByteArray,
        val headers: Map<String, List<String>>,
        val rsCode: Int,
        val lifetime: LocalDateTime,
    )

    @Nullable
    override fun getFilterName(): String? {
        return "Caching"
    }

    protected val cache: MutableMap<String, CacheObject> = HashMap()

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

    protected open fun getUri(request: HttpServletRequest): String {
        val queryString = if (request.queryString == null) "" else "?" + request.queryString
        return request.method + request.requestURI + queryString
    }

    protected open val scheduler =
        object : SchedulerTimer(clearDelay, "ClearRqRsCache") {
            override fun startBatch() {
                val now = LocalDateTime.now()
                cache
                    .entries
                    .stream()
                    .filter { (_, value): Map.Entry<String, CacheObject> -> value.lifetime.isBefore(now) }
                    .map { (key): Map.Entry<String, CacheObject> -> key }
                    .collect(Collectors.toSet())
                    .forEach(Consumer { key: String -> cache.remove(key) })
            }
        }

    companion object {
        const val CACHE_REQUEST_ATTRIBUTE_NAME = "CacheRequest"
        const val CACHE_REQUEST_LIFETIME_ATTRIBUTE_NAME = "CacheRequestLifetime"

        private fun setHeaders(
            response: ContentCachingResponseWrapper,
            cacheObject: CacheObject?,
        ) {
            cacheObject!!
                .headers
                .forEach(
                    BiConsumer { key: String?, value: List<String?> ->
                        value
                            .forEach(Consumer { v: String? -> response.setHeader(key, v) })
                    },
                )
        }
    }
}

/*
 * Copyright (c) 2023  Alcosi Group Ltd. and affiliates.
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

package com.alcosi.lib.filters

import jakarta.servlet.FilterChain
import jakarta.servlet.http.HttpServletRequest
import org.springframework.lang.Nullable
import org.springframework.scheduling.annotation.Scheduled
import org.springframework.web.util.ContentCachingResponseWrapper
import java.time.Duration
import java.time.LocalDateTime
import java.util.*
import java.util.function.BiConsumer
import java.util.function.Consumer
import java.util.stream.Collectors



open class CachingRqRsFilter (val refreshUri:String, maxBodySize:Int ): WrappedOnePerRequestFilter(maxBodySize) {
    @JvmRecord
    data class CacheObject(
        val body: ByteArray,
        val headers: Map<String, List<String>>,
        val rsCode: Int,
        val lifetime: LocalDateTime
    ) {
    }

    @Nullable
    override fun getFilterName(): String? {
        return "Caching"
    }

    private val cache: MutableMap<String, CacheObject> = HashMap()

    override fun doFilterWrapped(
        request: CachingRequestWrapper,
        response: ContentCachingResponseWrapper,
        filterChain: FilterChain
    ) {
        val uri = getUri(request)
        if(uri==refreshUri){
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
                    .forEach{ hn: String ->
                        headers[hn] = LinkedList(response.getHeaders(hn))
                    }
                val body = response.contentAsByteArray
                response.copyBodyToResponse()
                val lifetime =
                    request.getAttribute(CACHE_REQUEST_LIFETIME_ATTRIBUTE_NAME) as Duration ?: Duration.ofMinutes(10)
                val validTill = LocalDateTime.now().plus(lifetime)
                cache[uri] = CacheObject(body, headers, response.status, validTill)
                logger.info("Request $uri has been cached till $validTill")
            }
        }
    }

    private fun getUri(request: HttpServletRequest): String {
        val queryString = if (request.queryString == null) "" else "?" + request.queryString
        return request.method + request.requestURI + queryString
    }

    @Scheduled
    protected fun clear() {
        val now = LocalDateTime.now()
        cache
            .entries
            .stream()
            .filter { (_, value): Map.Entry<String, CacheObject> -> value.lifetime.isBefore(now) }
            .map { (key): Map.Entry<String, CacheObject> -> key }
            .collect(Collectors.toSet())
            .forEach(Consumer { key: String -> cache.remove(key) })
    }

    companion object {
        const val CACHE_REQUEST_ATTRIBUTE_NAME = "CacheRequest"
        const val CACHE_REQUEST_LIFETIME_ATTRIBUTE_NAME = "CacheRequestLifetime"
        private fun setHeaders(response: ContentCachingResponseWrapper, cacheObject: CacheObject?) {
            cacheObject!!
                .headers
                .forEach(BiConsumer { key: String?, value: List<String?> ->
                    value
                        .forEach(Consumer { v: String? -> response.setHeader(key, v) })
                })
        }
    }
}
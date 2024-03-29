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

package com.alcosi.lib.filters.servlet.log

import com.alcosi.lib.filters.servlet.ThreadContext
import com.alcosi.lib.filters.servlet.WrappedOnePerRequestFilter
import com.alcosi.lib.filters.servlet.cache.CachingRequestWrapper
import jakarta.servlet.FilterChain
import jakarta.servlet.http.Part
import org.springframework.beans.factory.annotation.Value
import org.springframework.http.MediaType
import org.springframework.lang.Nullable
import org.springframework.web.context.request.RequestAttributes
import org.springframework.web.context.request.RequestContextHolder
import org.springframework.web.util.ContentCachingResponseWrapper
import java.nio.charset.Charset
import java.util.*
import java.util.logging.Level
import java.util.logging.Logger

open class LoggingFilter(
    val logInternalService: LogInternalService,
    val threadContext: ThreadContext,
    @Value("\${common-lib.request_body_log.max.server:10000}") maxBodySize: Int,
) : WrappedOnePerRequestFilter(
        maxBodySize,
    ) {
    override fun doFilterWrapped(
        request: CachingRequestWrapper,
        response: ContentCachingResponseWrapper,
        filterChain: FilterChain,
    ) {
        val time = System.currentTimeMillis()
        try {
            val id = threadContext.getRqId()
            logInternalService.logRequest(request, id)
            filterChain.doFilter(request, response)
        } catch (t: Throwable) {
            logger.error("Error in request", t)
        } finally {
            logInternalService.afterRequest(request, response, time)
        }
    }

    @Nullable
    override fun getFilterName(): String? {
        return "Logging"
    }

    open class LogInternalService(
        val loggingLevel: Level,
        val maxBodySize: Int,
        val threadContext: ThreadContext,
    ) {
        open fun afterRequest(
            request: CachingRequestWrapper,
            response: ContentCachingResponseWrapper,
            time: Long,
        ) {
            try {
                if (loggingLevel == Level.OFF) {
                    return
                }
                val id = threadContext.getRqId()
                response.setHeader("RQ_ID", id)
                logResponse(request, response, id, time)
                response.copyBodyToResponse()
            } catch (t: Throwable) {
                logger.log(Level.SEVERE, "RqRs error !", t)
            }
        }

        open fun logRequest(
            request: CachingRequestWrapper,
            rqId: String,
        ) {
            if (loggingLevel == Level.OFF) {
                return
            }
            logSessionId(request)
            val contentEncoding = Charset.forName(request.characterEncoding ?: "UTF-8")
            try {
                val content =
                    if (request.contentType != null && request.contentType.startsWith("multipart/form-data")) {
                        val parts: List<Part> = ArrayList(request.parts)
                        parts.filter { it.size < maxBodySize }.map { "${it.name}:${it.contentType}:${it.size}" }
                            .joinToString(";")
                    } else {
                        String(request.body, contentEncoding)
                    }
                val queryString = if (request.queryString == null) "" else "?" + request.queryString
                val headers =
                    Collections.list(request.headerNames).map { headerName: String ->
                        "$headerName:${Collections.list(request.getHeaders(headerName)).joinToString(";")}"
                    }.joinToString(";")
                logger.info("RQ|= ${request.method} ${request.requestURI}$queryString")
                val logString = constructRqBody(rqId, request, queryString, headers, content)
                logger.log(loggingLevel, logString)
            } catch (t: Throwable) {
                logger.log(Level.SEVERE, "", t)
            }
        }

        protected open fun constructRqBody(
            rqId: String,
            request: CachingRequestWrapper,
            queryString: String,
            headers: String,
            content: String,
        ): String {
            val logString =
                """
                
                ===========================SERVER request begin===========================
                =ID           : $rqId
                =URI          : ${request.method} ${request.requestURI}$queryString
                =Headers      : $headers    
                =Body         : $content
                ===========================SERVER request end   ==========================
                """.trimIndent()
            return logString
        }

        protected open fun logSessionId(request: CachingRequestWrapper) {
            val sessionid =
                Optional.ofNullable(request.getHeader("Authorization")).map { obj: String ->
                    obj.uppercase(
                        Locale.getDefault(),
                    )
                }
                    .orElseGet { UUID.randomUUID().toString().replace("-", "").uppercase(Locale.getDefault()) }
            val attrs = RequestContextHolder.getRequestAttributes()
            attrs?.setAttribute("SESSION_ID", sessionid, RequestAttributes.SCOPE_REQUEST)
        }

        protected open fun logResponse(
            request: CachingRequestWrapper,
            response: ContentCachingResponseWrapper,
            rqId: String,
            time: Long,
        ) {
            val status = response.status
            val queryString = if (request.queryString == null) "" else "?" + request.queryString
            val headers =
                response.headerNames.map { headerName: String ->
                    "$headerName:${response.getHeaders(headerName).joinToString(";")}"
                }.joinToString(";")
            val content =
                if (MediaType.APPLICATION_OCTET_STREAM.toString().equals(response.contentType, ignoreCase = true) ||
                    MediaType.IMAGE_PNG.toString().equals(response.contentType, ignoreCase = true) ||
                    (response.contentType ?: "").lowercase().contains("html")
                ) {
                    FILE_RS
                } else {
                    String(response.contentAsByteArray, Charset.forName(response.characterEncoding))
                }
            val logBody = constructRsBody(rqId, status, request, queryString, time, headers, content)
            logger.log(loggingLevel, logBody)
        }

        protected open fun constructRsBody(
            rqId: String,
            status: Int,
            request: CachingRequestWrapper,
            queryString: String,
            time: Long,
            headers: String,
            content: String,
        ): String {
            val logBody =
                """
                
                ===========================SERVER response begin===========================
                =ID           : $rqId
                =URI          : $status ${request.method} ${request.requestURI}$queryString
                =Took         : ${System.currentTimeMillis() - time} ms
                =Headers      : $headers    
                =Body         : $content
                ===========================SERVER response end   ==========================
                """.trimIndent()
            return logBody
        }
    }

    companion object {
        val logger = Logger.getLogger(this::class.java.name)
        val FILE_RS = "<file_bytes>"
    }
}

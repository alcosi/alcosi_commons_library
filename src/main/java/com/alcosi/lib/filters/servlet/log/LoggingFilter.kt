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
import com.alcosi.lib.filters.servlet.log.ServletLoggingFilterConfig.Companion.LOG_CONFIG_ATTRIBUTE
import jakarta.servlet.FilterChain
import jakarta.servlet.http.HttpServletRequest
import jakarta.servlet.http.Part
import org.springframework.http.MediaType
import org.springframework.lang.Nullable
import org.springframework.web.context.request.RequestAttributes
import org.springframework.web.context.request.RequestContextHolder
import org.springframework.web.util.ContentCachingResponseWrapper
import java.nio.charset.Charset
import java.util.*
import java.util.logging.Level
import java.util.logging.Logger

/**
 * A filter class for logging HTTP requests and responses.
 *
 * @param logInternalService The logging service responsible for handling the logging operations.
 * @param threadContext The thread context used for retrieving the request ID.
 * @param maxBodySize The maximum body size for logging the request.
 */
open class LoggingFilter(
    val logInternalService: LogInternalService,
    val threadContext: ThreadContext,
    maxBodySize: Int,
) : WrappedOnePerRequestFilter(maxBodySize) {
    /**
     * Applies the filter to the incoming HTTP request and*/
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
            super.logger.error("Error in request", t)
        } finally {
            logInternalService.afterRequest(request, response, time)
        }
    }

    /**
     * Determines whether the given HTTP request should be filtered by the LoggingFilter.
     *
     * @param request The HttpServletRequest object representing the incoming HTTP request.
     * @return true if the request should not be filtered, false otherwise.
     */
    override fun shouldNotFilter(request: HttpServletRequest): Boolean {
        val config = (request.getAttribute(LOG_CONFIG_ATTRIBUTE) as ServletLoggingFilterConfig?) ?: ServletLoggingFilterConfig()
        return !config.enabled
    }

    /**
     * Retrieves the name of the filter.
     *
     * @return The name of the filter as a String, or null if the name is not set.
     */
    @Nullable
    override fun getFilterName(): String? = "Logging"

    /**
     * A class that provides logging functionality for internal service requests and responses.
     *
     * @property loggingLevel The logging level for the service.
     * @property maxBodySize The maximum size of the request/response body.
     * @property threadContext The thread context for the service.
     */
    open class LogInternalService(
        val loggingLevel: Level,
        val maxBodySize: Int,
        val threadContext: ThreadContext,
    ) {
        /**
         * Performs necessary actions after processing an HTTP request.
         *
         * @param request The wrapped HTTP request.
         * @param response The wrapped HTTP response.
         * @param time The time taken to process the request in milliseconds.
         */
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

        /**
         * Logs the HTTP request.
         *
         * @param request The wrapped HTTP request.
         * @param rqId The unique ID for the request.
         */
        open fun logRequest(
            request: CachingRequestWrapper,
            rqId: String,
        ) {
            if (loggingLevel == Level.OFF) {
                return
            }
            logSessionId(request)
            val contentEncoding = Charset.forName(request.characterEncoding ?: "UTF-8")
            val config = getLoggingFilterConfig(request)

            try {
                val content =
                    if (config.logBody) {
                        if (request.contentType != null && request.contentType.startsWith("multipart/form-data")) {
                            val parts: List<Part> = ArrayList(request.parts)
                            parts
                                .filter { it.size < maxBodySize }
                                .map { "${it.name}:${it.contentType}:${it.size}" }
                                .joinToString(";")
                        } else {
                            String(request.body, contentEncoding)
                        }
                    } else {
                        MASKED
                    }
                val queryString = if (request.queryString == null) "" else "?" + request.queryString
                val headers =
                    if (config.logHeaders) {
                        Collections
                            .list(request.headerNames)
                            .map { headerName: String ->
                                "$headerName:${Collections.list(request.getHeaders(headerName)).joinToString(";")}"
                            }.joinToString(";")
                    } else {
                        MASKED
                    }
                val uriString = if (config.logUri) "${request.method} ${request.requestURI}$queryString" else MASKED
                val logString = constructRqBody(rqId, uriString, headers, content)
                logger.log(loggingLevel, logString)
            } catch (t: Throwable) {
                logger.log(Level.SEVERE, "", t)
            }
        }

        protected open fun getLoggingFilterConfig(request: CachingRequestWrapper) = (request.getAttribute(LOG_CONFIG_ATTRIBUTE) as ServletLoggingFilterConfig?) ?: ServletLoggingFilterConfig()

        /**
         * Constructs the log string for the HTTP request body.
         *
         * @param rqId The unique ID for the request.
         * @param uriString URI of request.
         * @param headers The headers of the request.
         * @param content The body content of the request.
         * @return The log string representing the request body.
         */
        protected open fun constructRqBody(
            rqId: String,
            uriString: String,
            headers: String,
            content: String,
        ): String {
            val logString =
                """
                
                ===========================SERVER request begin===========================
                =ID           : $rqId
                =URI          : $uriString
                =Headers      : $headers    
                =Body         : $content
                ===========================SERVER request end   ==========================
                """.trimIndent()
            return logString
        }

        /**
         * Logs the session ID for the request.
         *
         * @param request The wrapped HTTP request.
         */
        protected open fun logSessionId(request: CachingRequestWrapper) {
            val sessionid =
                Optional
                    .ofNullable(request.getHeader("Authorization"))
                    .map { obj: String ->
                        obj.uppercase(
                            Locale.getDefault(),
                        )
                    }.orElseGet {
                        UUID
                            .randomUUID()
                            .toString()
                            .replace("-", "")
                            .uppercase(Locale.getDefault())
                    }
            val attrs = RequestContextHolder.getRequestAttributes()
            attrs?.setAttribute("SESSION_ID", sessionid, RequestAttributes.SCOPE_REQUEST)
        }

        /**
         * Logs the HTTP response.
         *
         * @param request*/
        protected open fun logResponse(
            request: CachingRequestWrapper,
            response: ContentCachingResponseWrapper,
            rqId: String,
            time: Long,
        ) {
            val config = getLoggingFilterConfig(request)
            val status = response.status
            val queryString = if (request.queryString == null) "" else "?" + request.queryString
            val headers =
                if (config.logHeaders) {
                    response.headerNames
                        .map { headerName: String ->
                            "$headerName:${response.getHeaders(headerName).joinToString(";")}"
                        }.joinToString(";")
                } else {
                    MASKED
                }
            val content =
                if (config.logBody) {
                    if (MediaType.APPLICATION_OCTET_STREAM.toString().equals(response.contentType, ignoreCase = true) ||
                        MediaType.IMAGE_PNG.toString().equals(response.contentType, ignoreCase = true) ||
                        (response.contentType ?: "").lowercase().contains("html")
                    ) {
                        FILE_RS
                    } else {
                        String(response.contentAsByteArray, Charset.forName(response.characterEncoding))
                    }
                } else {
                    MASKED
                }
            val uriString = if (config.logUri) "$status ${request.method} ${request.requestURI}$queryString" else MASKED
            val logBody = constructRsBody(rqId, uriString, time, headers, content)
            logger.log(loggingLevel, logBody)
        }

        /**
         * Constructs the log string for the HTTP response body.
         *
         * @param rqId The unique ID for the request.
         * @param uriString The URI string of the request.
         * @param time The time taken to process the request in milliseconds.
         * @param headers The headers of the request.
         * @param content The body content of the request.
         * @return The log string representing the response body.
         */
        protected open fun constructRsBody(
            rqId: String,
            uriString: String,
            time: Long,
            headers: String,
            content: String,
        ): String {
            val logBody =
                """
                
                ===========================SERVER response begin===========================
                =ID           : $rqId
                =URI          : $uriString
                =Took         : ${System.currentTimeMillis() - time} ms
                =Headers      : $headers    
                =Body         : $content
                ===========================SERVER response end   ==========================
                """.trimIndent()
            return logBody
        }
    }

    /**
     * The [Companion] class provides a set of companion properties and functions.
     *
     * @property logger The logger instance for logging.
     * @property FILE_RS The placeholder for the response file bytes.
     */
    companion object {
        val logger = Logger.getLogger(this::class.java.name)
        val FILE_RS = "<file_bytes>"
        val MASKED = "<MASKED>"
    }
}

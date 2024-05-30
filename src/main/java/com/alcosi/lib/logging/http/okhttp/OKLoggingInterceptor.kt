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

package com.alcosi.lib.logging.http.okhttp

import com.alcosi.lib.filters.servlet.HeaderHelper
import okhttp3.*
import okhttp3.ResponseBody.Companion.toResponseBody
import okio.Buffer
import org.springframework.core.Ordered
import java.util.*
import java.util.logging.Level
import java.util.logging.Logger

/**
 * OKLoggingInterceptor is an open class that implements the Interceptor interface and Ordered interface.
 * It intercepts the HTTP requests and logs the request and response information.
 *
 * @property maxBodySize The maximum size of the response body to log.
 * @property loggingLevel The logging level for the interceptor.
 * @property headerHelper An instance of the HeaderHelper class to handle headers.
 * @property order The order of the interceptor in the chain.
 */
open class OKLoggingInterceptor(val maxBodySize: Int, val loggingLevel: Level, val headerHelper: HeaderHelper, private val order: Int) : Interceptor, Ordered {
    /**
     * Variable to handle logging functionality.
     * Uses the java.util.logging.Logger class to log messages.
     */
    val logger = Logger.getLogger(this.javaClass.name)

    /**
     * Intercepts the request and logs the request and response.
     *
     * @param chain the interceptor chain
     * @return the intercepted response
     * @throws Exception if an error occurs while intercepting the request
     */
    override fun intercept(chain: Interceptor.Chain): Response {
        return try {
            val t1 = System.currentTimeMillis()
            val rqId = getIdString()
            val httpRequest = chain.request()
            logRq(rqId, httpRequest)
            logRs(rqId, chain.proceed(httpRequest), httpRequest, t1)
        } catch (e: Exception) {
            logger.log(Level.SEVERE, "interceptor error ", e)
            throw e
        }
    }

    /**
     * Logs the response of a network request.
     *
     * @param rqId      The ID of the request.
     * @param response  The response obtained from the network request.
     * @param request   The original request made.
     * @param time      The time taken for the network request.
     * @return          The modified response with log information.
     */
    protected open fun logRs(
        rqId: String,
        response: Response,
        request: Request,
        time: Long,
    ): Response {
        if (loggingLevel == Level.OFF) {
            return response
        }
        val contentType = response.body?.contentType()
        val contentLength = response.body?.contentLength() ?: 0
        val content = if (contentLength > maxBodySize) "<TOO BIG $contentLength bytes>" else response.body?.string()
        val wrappedBody = (content ?: "").toResponseBody(contentType)
        val logBody = constructRsBody(rqId, response, request, time, content)
        logger.log(loggingLevel, logBody)
        return response.newBuilder().body(wrappedBody).build()
    }

    /**
     * Constructs the response body for logging.
     *
     * @param rqId      The ID of the request.
     * @param response  The response obtained from the network request.
     * @param request   The original request made.
     * @param time      The time taken for the network request.
     * @param content   The content of the response body.
     * @return          The constructed response body string.
     */
    protected open fun constructRsBody(
        rqId: String,
        response: Response,
        request: Request,
        time: Long,
        content: String?,
    ): String {
        val logBody =
            """
            
            ===========================CLIENT OKHttp response begin===========================
            =ID           : $rqId
            =URI          : ${response.code} ${request.method} ${request.url}
            =Took         : ${System.currentTimeMillis() - time} ms
            =Headers      : ${getHeaders(response.headers)}    
            =Body         : $content
            ===========================CLIENT OKHttp response end   ==========================
            """.trimIndent()
        return logBody
    }

    /**
     * Converts the given Headers object into a formatted string containing all headers.
     *
     * @param headers The Headers object to convert.
     */
    protected open fun getHeaders(headers: Headers) =
        (
            headers.toMultimap().map {
                "${it.key}:${it.value.joinToString(
                    ",",
                )}"
            }.joinToString(";")
        )

    /**
     * Logs the request details.
     *
     * @param rqId The ID of the request.
     * @param request The Request object containing the request details.
     */
    protected open fun logRq(
        rqId: String,
        request: Request,
    ) {
        if (loggingLevel == Level.OFF) {
            return
        }
        val requestBuffer = Buffer()

        val contentLength = request.body?.contentLength() ?: 0L
        val emptyBody = ((contentLength == 0L) || request.body == null)
        if (!emptyBody) {
            request.body!!.writeTo(requestBuffer)
        }
        val body = if (contentLength > maxBodySize) "<TOO BIG $contentLength bytes>" else requestBuffer.readUtf8()
        val logString = constructRqBody(rqId, request, body)
        logger.log(loggingLevel, logString)
    }

    /**
     * Constructs the request body for logging.
     *
     * @param rqId      The ID of the request.
     * @param request   The request object containing the request details.
     * @param body      The body of the request.
     * @return          The constructed request body string.
     */
    protected open fun constructRqBody(
        rqId: String,
        request: Request,
        body: String,
    ): String {
        val logString =
            """
            
            ===========================CLIENT OKHttp request begin===========================
            =ID           : $rqId
            =URI          : ${request.method} ${request.url}
            =Headers      : ${getHeaders(request.headers)}    
            =Body         : $body
            ===========================CLIENT OKHttp request end   ==========================
            """.trimIndent()
        return logString
    }

    companion object {
        /**
         * Random number generator.
         */
        private val RANDOM = Random()

        /**
         * Generates a unique ID string.
         *
         * @return The generated ID string.
         */
        protected fun getIdString(): String {
            val integer = RANDOM.nextInt(10000000)
            val leftPad = integer.toString().padStart(7, '0')
            return leftPad.substring(0, 4) + '-' + leftPad.substring(5)
        }
    }

    /**
     * Returns the order value of this interceptor.
     *
     * @return The order value of this interceptor.
     */
    override fun getOrder(): Int {
        return order
    }
}

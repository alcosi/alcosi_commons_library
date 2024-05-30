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

package com.alcosi.lib.logging.http.resttemplate

import com.alcosi.lib.filters.servlet.HeaderHelper
import org.springframework.core.Ordered
import org.springframework.http.HttpHeaders
import org.springframework.http.HttpRequest
import org.springframework.http.client.ClientHttpRequestExecution
import org.springframework.http.client.ClientHttpRequestInterceptor
import org.springframework.http.client.ClientHttpResponse
import java.nio.charset.StandardCharsets
import java.util.*
import java.util.function.Consumer
import java.util.logging.Level
import java.util.logging.Logger

/**
 * A filter class for logging the request and response of a RestTemplate.
 *
 * @param maxBodySize The maximum size of the request/response body to log.
 * @param loggingLevel The logging level for the filter.
 * @param headerHelper The header helper class for creating request headers.
 * @param order The order of the filter in the filter chain.
 */
open class RestTemplateLogRequestResponseFilter(
    val maxBodySize: Int,
    val loggingLevel: Level,
    val headerHelper: HeaderHelper,
    private val order: Int,
) : ClientHttpRequestInterceptor, Ordered {
    /**
     * Intercepts the client HTTP request and response.
     *
     * @param request The HTTP request to be intercepted.
     * @param body The body of the HTTP request.
     * @param execution The execution object that allows executing the intercepted request.
     * @return The client HTTP response.
     */
    override fun intercept(
        request: HttpRequest,
        body: ByteArray,
        execution: ClientHttpRequestExecution,
    ): ClientHttpResponse {
        val time = System.currentTimeMillis()
        val id = headerHelper.getContextRqId()
        traceRequest(id, request, body)
        val response: ClientHttpResponse = execution.execute(request, body)
        traceResponse(id, response, request, time)
        return response
    }

    /**
     * Traces the request with the given [id], [request], and [body].
     * If the logging level is [Level.OFF], the method returns without performing any logging.
     * The [body] is converted to a string representation, either as the original string if its size is within the maximum allowed size,
     * or as a truncated string if its size exceeds the maximum allowed size.
     * The resulting log body is constructed using the [constructRqBody] method, and logged using the [logger] with the specified [loggingLevel].
     *
     * @param id The identifier of the request.
     * @param request The HTTP request object.
     * @param body The request body as a byte array.
     */
    protected open fun traceRequest(
        id: String,
        request: HttpRequest,
        body: ByteArray,
    ) {
        if (loggingLevel == Level.OFF) {
            return
        }
        val contentLength = body.size
        val bodyString =
            if (contentLength > maxBodySize) {
                "<TOO BIG $contentLength bytes>"
            } else {
                String(
                    body,
                    StandardCharsets.UTF_8,
                )
            }
        val logBody = constructRqBody(id, request, bodyString)
        logger.log(loggingLevel, logBody)
    }

    /**
     * Constructs the request body for logging purposes.
     *
     * @param id The unique identifier for*/
    protected open fun constructRqBody(
        id: String,
        request: HttpRequest,
        bodyString: String,
    ): String {
        val logBody =
            """
            
            ===========================CLIENT REST request begin===========================
            =ID           : $id
            =URI          : ${request.method} ${request.uri}
            =Headers      : ${headersToString(request.headers)}    
            =Body         : $bodyString
            ===========================CLIENT REST request end   ==========================
            """.trimIndent()
        return logBody
    }

    /**
     * Traces the response received from the client.
     *
     * @*/
    protected open fun traceResponse(
        id: String,
        response: ClientHttpResponse,
        request: HttpRequest,
        time: Long,
    ) {
        if (loggingLevel == Level.OFF) {
            return
        }
        val contentLength = response.headers.contentLength
        val bodyString =
            if (contentLength > maxBodySize) {
                "<TOO BIG $contentLength bytes>"
            } else {
                val readAllBytes = response.body.readAllBytes()
                String(readAllBytes, StandardCharsets.UTF_8)
            }
        val logBody = constructRsBody(id, response, request, time, bodyString)
        logger.log(loggingLevel, logBody)
    }

    /**
     * Constructs a response body string for logging purposes.
     *
     * @param id the*/
    protected open fun constructRsBody(
        id: String,
        response: ClientHttpResponse,
        request: HttpRequest,
        time: Long,
        bodyString: String,
    ): String {
        val logBody =
            """
            
            ===========================CLIENT REST response begin===========================
            =ID           : $id
            =URI          : ${response.statusCode} ${request.method} ${request.uri}
            =Took         : ${System.currentTimeMillis() - time} ms
            =Headers      : ${headersToString(response.headers)}    
            =Body         : $bodyString
            ===========================CLIENT REST response end   ==========================
            """.trimIndent()
        return logBody
    }

    /**
     * Convert the given HttpHeaders object to a String representation of headers.
     *
     * @param headers The HttpHeaders object to convert.
     * @return A semicolon-separated string representing the headers in the format "key1:value1;key2:value2;...".
     */
    protected open fun headersToString(headers: HttpHeaders): String {
        val list: MutableList<String> = LinkedList<String>()
        headers.forEach { key: String, value: List<String> ->
            value.forEach(
                Consumer { v: String -> list.add("$key:$v") },
            )
        }
        return java.lang.String.join(";", list)
    }

    companion object {
        val logger = Logger.getLogger(this.javaClass.name)
    }

    /**
     * Returns the order of the item.
     *
     * @return The order of the item as an integer value.
     */
    override fun getOrder(): Int {
        return order
    }
}

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

open class RestTemplateLogRequestResponseFilter(
    val maxBodySize: Int,
    val loggingLevel: Level,
    val headerHelper: HeaderHelper,
    private val order: Int,
) : ClientHttpRequestInterceptor, Ordered {
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

    override fun getOrder(): Int {
        return order
    }
}

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

open class OKLoggingInterceptor(val maxBodySize: Int, val loggingLevel: Level, val headerHelper: HeaderHelper, private val order: Int) : Interceptor, Ordered {
    val logger = Logger.getLogger(this.javaClass.name)

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

    protected open fun logRs(
        rqId: String,
        response: Response,
        request: Request,
        time: Long,
    ): Response {
        if (loggingLevel == Level.OFF) {
            return response
        }
        val t2 = System.currentTimeMillis()
        val contentType = response.body?.contentType()
        val contentLength = response.body?.contentLength() ?: 0
        val content = if (contentLength > maxBodySize) "<TOO BIG $contentLength bytes>" else response.body?.string()
        val wrappedBody = (content ?: "").toResponseBody(contentType)
        val logBody = constructRsBody(rqId, response, request, time, content)
        logger.log(loggingLevel, logBody)
        return response.newBuilder().body(wrappedBody).build()
    }

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

    protected open fun getHeaders(headers: Headers) =
        (
            headers.toMultimap().map {
                "${it.key}:${it.value.joinToString(
                    ",",
                )}"
            }.joinToString(";")
        )

    protected open fun logRq(
        rqId: String,
        request: Request,
    ) {
        if (loggingLevel == Level.OFF) {
            return
        }
        val sb = StringBuilder()
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
        private val RANDOM = Random()

        protected fun getIdString(): String {
            val integer = RANDOM.nextInt(10000000)
            val leftPad = integer.toString().padStart(7, '0')
            return leftPad.substring(0, 4) + '-' + leftPad.substring(5)
        }
    }

    override fun getOrder(): Int {
        return order
    }
}

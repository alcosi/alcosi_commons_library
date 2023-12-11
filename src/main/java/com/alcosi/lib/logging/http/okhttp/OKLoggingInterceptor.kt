/*
 * Copyright (c) 2024  Alcosi Group Ltd. and affiliates.
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

package com.alcosi.lib.logging.http.okhttp

import com.alcosi.lib.filters.servlet.HeaderHelper
import okhttp3.*
import okhttp3.ResponseBody.Companion.toResponseBody
import okio.Buffer
import java.util.*
import java.util.logging.Level
import java.util.logging.Logger

open class OKLoggingInterceptor(val maxBodySize: Int, val loggingLevel: Level, val headerHelper: HeaderHelper) : Interceptor {
    val logger = Logger.getLogger(this.javaClass.name)

    override fun intercept(chain: Interceptor.Chain): Response {
        return try {
            val t1 = System.currentTimeMillis()
            val rqId = getIdString()
            val httpRequest = addRequestHeaders(chain)
            logRq(rqId, httpRequest)
            logRs(rqId, chain.proceed(httpRequest), httpRequest, t1)
        } catch (e: Exception) {
            logger.log(Level.SEVERE, "interceptor error ", e)
            throw e
        }
    }

    private fun addRequestHeaders(chain: Interceptor.Chain): Request {
        val httpRequestBuilder = chain.request().newBuilder()
        headerHelper.createRequestHeadersMap().forEach {
            httpRequestBuilder.addHeader(it.key, it.value)
        }
        val httpRequest = httpRequestBuilder.build()
        return httpRequest
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
}

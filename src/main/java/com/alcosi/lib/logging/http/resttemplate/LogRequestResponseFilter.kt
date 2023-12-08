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

package com.alcosi.lib.logging.http.resttemplate

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


open class LogRequestResponseFilter(val maxBodySize: Int, val loggingLevel: Level) :
    ClientHttpRequestInterceptor{

    override fun intercept(
        request: HttpRequest,
        body: ByteArray,
        execution: ClientHttpRequestExecution
    ): ClientHttpResponse {
        val time = System.currentTimeMillis()
        val id = getIdString()
        traceRequest(id, request, body)
        val response: ClientHttpResponse = execution.execute(request, body)
        traceResponse(id, response, request, time)
        return response
    }

    private fun traceRequest(id: String, request: HttpRequest, body: ByteArray) {
        if ( loggingLevel == Level.OFF){
            return
        }
        val contentLength=body.size
        val bodyString = if (contentLength >maxBodySize) "<TOO BIG ${contentLength} bytes>" else String(body, StandardCharsets.UTF_8)
        val logBody = """

===========================CLIENT REST request begin===========================
=ID           : ${id}
=URI          : ${request.method} ${request.uri}
=Headers      : ${headersToString(request.headers)}    
=Body         : $bodyString
===========================CLIENT REST request end   ==========================
        """.trimIndent()
        logger.log(loggingLevel, logBody)
    }

    private fun traceResponse(id: String, response: ClientHttpResponse, request: HttpRequest, time: Long) {
        if ( loggingLevel == Level.OFF){
            return
        }
        val contentLength=response.headers.contentLength;
        val bodyString=if (contentLength >maxBodySize) "<TOO BIG ${contentLength} bytes>" else {
            val readAllBytes = response.body.readAllBytes()
            String(readAllBytes,StandardCharsets.UTF_8)
        }
        val logBody = """

===========================CLIENT REST response begin===========================
=ID           : ${id}
=URI          : ${response.statusCode} ${request.method} ${request.uri}
=Took         : ${System.currentTimeMillis() - time} ms
=Headers      : ${headersToString(response.headers)}    
=Body         : ${bodyString}
===========================CLIENT REST response end   ==========================
        """.trimIndent()
        logger.log(loggingLevel, logBody)
    }

    private fun headersToString(headers: HttpHeaders): String {
        val list: MutableList<String> = LinkedList<String>()
        headers.forEach { key: String, value: List<String> ->
            value.forEach(
                Consumer { v: String -> list.add("$key:$v") })
        }
        return java.lang.String.join(";", list)
    }

    companion object {
        private val RANDOM = Random()
        val logger=Logger.getLogger(this.javaClass.name)

        fun getIdString(): String {
            val integer = RANDOM.nextInt(10000000)
            val leftPad = integer.toString().padStart(7,'0')
            return leftPad.substring(0, 4) + '-' + leftPad.substring(5)
        }
    }
}
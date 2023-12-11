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

package com.alcosi.lib.filters.router

import com.alcosi.lib.dto.APIError
import com.alcosi.lib.exception.ApiException
import org.springframework.http.converter.HttpMessageConversionException
import org.springframework.web.server.ResponseStatusException
import org.springframework.web.servlet.function.HandlerFunction
import org.springframework.web.servlet.function.ServerRequest
import org.springframework.web.servlet.function.ServerResponse
import java.util.logging.Level
import java.util.logging.Logger

open class CaughtExceptionRouterFilter(val messageConversionErrorCode: Int, val unknownErrorCode: Int) : RouterFilter {
    override fun filter(
        request: ServerRequest,
        next: HandlerFunction<ServerResponse>,
    ): ServerResponse {
        try {
            return next.handle(request)
        } catch (t: Throwable) {
            logger.log(Level.SEVERE, "Error handling request", t)
            return toServerResponse(t)
        }
    }

    protected open fun toServerResponse(t: Throwable): ServerResponse {
        val apiError =
            when (t) {
                is ApiException -> APIError(t.reason ?: "", t.code.toInt(), t.javaClass.simpleName, t.httpCode)
                is ResponseStatusException -> APIError(t.reason ?: "", t.statusCode.value(), t.javaClass.simpleName, t.statusCode.value())
                is HttpMessageConversionException -> APIError(t.message ?: "", messageConversionErrorCode, t.javaClass.simpleName)
                else -> APIError(t.message ?: "", unknownErrorCode, t.javaClass.simpleName)
            }
        return ServerResponse.status(apiError.httpCode).body(apiError)
    }

    companion object {
        val logger = Logger.getLogger(this.javaClass.name)
    }
}

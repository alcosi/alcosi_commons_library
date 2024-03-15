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

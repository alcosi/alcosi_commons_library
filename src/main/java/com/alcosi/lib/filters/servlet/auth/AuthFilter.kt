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

package com.alcosi.lib.filters.servlet.auth

import com.alcosi.lib.dto.APIError
import com.alcosi.lib.filters.servlet.HeaderHelper
import com.fasterxml.jackson.databind.ObjectMapper
import jakarta.servlet.FilterChain
import jakarta.servlet.http.HttpServletRequest
import jakarta.servlet.http.HttpServletResponse
import org.springframework.http.MediaType
import org.springframework.web.filter.OncePerRequestFilter

open class AuthFilter(
    protected val accessKey: String,
    protected val helper: HeaderHelper,
    protected val objectMapper: ObjectMapper,
    protected val wrongEnvErrorCode: Int,
    protected val wrongAccessKeyErrorCode: Int,
    protected val noAccessKeyErrorCode: Int,
) : OncePerRequestFilter() {
    override fun doFilterInternal(
        request: HttpServletRequest,
        response: HttpServletResponse,
        filterChain: FilterChain,
    ) {
        if (!request.getHeader(HeaderHelper.ENV_HEADER).equals(helper.environment, true)) {
            val error =
                APIError(
                    "Wrong environment ${request.getHeader(HeaderHelper.ENV_HEADER)}/${helper.environment}",
                    wrongEnvErrorCode,
                    "",
                )
            writeError(response, error)
            return
        }
        val headerAccessKey = request.getHeader(HeaderHelper.SERVICE_AUTH_HEADER)?.split(" ")?.get(1)
        if (!headerAccessKey.equals(accessKey)) {
            val error =
                APIError(
                    if (headerAccessKey == null) "Missing microservice access key" else "Wrong microservice access key",
                    if (headerAccessKey == null) noAccessKeyErrorCode else wrongAccessKeyErrorCode,
                    "",
                )
            writeError(response, error)
            return
        }
        filterChain.doFilter(request, response)
    }

    protected fun writeError(
        response: HttpServletResponse,
        error: APIError,
    ) {
        response.status = error.httpCode
        response.outputStream.write(objectMapper.writeValueAsBytes(error))
        response.contentType = MediaType.APPLICATION_JSON_VALUE
    }
}

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

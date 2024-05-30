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

/**
 * AuthFilter is a servlet filter responsible for authentication and authorization.
 *
 * @property accessKey The access key used for authentication.
 * @property helper The instance of the HeaderHelper used for handling request headers.
 * @property objectMapper The instance of the ObjectMapper used for serializing/deserializing JSON.
 * @property wrongEnvErrorCode The error code to be returned when the environment header is incorrect.
 * @property wrongAccessKeyErrorCode The error code to be returned when the access key is incorrect.
 * @property noAccessKeyErrorCode The error code to be returned when no access key is provided.
 *
 * @constructor Creates an AuthFilter instance with the given accessKey, helper, objectMapper, wrongEnvErrorCode,
 * wrongAccessKeyErrorCode, and noAccessKeyErrorCode.
 */
open class AuthFilter(
    protected val accessKey: String,
    protected val helper: HeaderHelper,
    protected val objectMapper: ObjectMapper,
    protected val wrongEnvErrorCode: Int,
    protected val wrongAccessKeyErrorCode: Int,
    protected val noAccessKeyErrorCode: Int,
) : OncePerRequestFilter() {
    /**
     * Applies the authentication and authorization logic for an incoming request.
     * If the request's environment header does not match the configured environment, an error response is written.
     * If the microservice access key in the request's header does not match the configured access key, an error response is written.
     * Otherwise, the request is passed to the next filter in the filter chain.
     *
     * @param request The incoming HttpServletRequest object.
     * @param response The outgoing HttpServletResponse object.
     * @param filterChain The FilterChain object representing the remaining filters in the chain.
     */
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

    /**
     * Writes an error response to the HttpServletResponse.
     *
     * @param response the HttpServletResponse object to write the error response to
     * @param error the APIError object containing the error details
     */
    protected fun writeError(
        response: HttpServletResponse,
        error: APIError,
    ) {
        response.status = error.httpCode
        response.outputStream.write(objectMapper.writeValueAsBytes(error))
        response.contentType = MediaType.APPLICATION_JSON_VALUE
    }
}

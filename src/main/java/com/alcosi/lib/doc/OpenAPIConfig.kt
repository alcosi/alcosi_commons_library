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

package com.alcosi.lib.doc

import io.github.breninsul.servlet.logging.logResponseBody
import org.apache.commons.io.IOUtils
import org.springframework.boot.autoconfigure.AutoConfiguration
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty
import org.springframework.boot.context.properties.EnableConfigurationProperties
import org.springframework.context.annotation.Bean
import org.springframework.http.HttpHeaders
import org.springframework.http.MediaType
import org.springframework.web.servlet.function.RouterFunction
import org.springframework.web.servlet.function.RouterFunctions
import org.springframework.web.servlet.function.ServerRequest
import org.springframework.web.servlet.function.ServerResponse
import java.util.logging.Level
import java.util.logging.Logger

/**
 * Configuration class for OpenAPI.
 *
 * This class provides configuration for serving OpenAPI documentation files and handling requests for those files.
 *
 * The configuration can be customized using properties defined in OpenAPIProperties.
 */
@AutoConfiguration
@ConditionalOnProperty(
    prefix = "common-lib.openapi",
    name = ["disabled"],
    matchIfMissing = true,
    havingValue = "false",
)
@EnableConfigurationProperties(OpenAPIProperties::class)
class OpenAPIConfig {
    /**
     * Regular expression pattern for validating allowed file names.
     * The pattern allows file names consisting of alphanumeric characters, underscores, hyphens, and parentheses,
     * followed by a dot and a file extension of 1-4 characters.
     *
     * Example usage:
     * val fileName = "example_file.txt"
     * val isFileNameValid = allowedFileRegex.matches(fileName)
     */
    val allowedFileRegex: Regex = "^([a-zA-Z0-9_\\-()])+(\\.\\w{1,4})\$".toRegex()

    /**
     * Represents a logger instance for logging messages and exceptions.
     */
    val logger = Logger.getLogger(this.javaClass.name)

    /**
     * Constructs an error response based on the given throwable.
     *
     * @param t The throwable.
     * @return The constructed server response.
     */
    @Bean
    @ConditionalOnMissingBean(OpenDocErrorConstructor::class)
    fun openApiErrorConstructor(): OpenDocErrorConstructor.Default = OpenDocErrorConstructor.Default()

    /**
     * Creates a RouterFunction for handling OpenAPI routes.
     *
     * @param openAPIProperties The OpenAPIProperties object containing configuration properties.
     * @param errorConstructor The OpenDocErrorConstructor used for constructing error responses.
     * @return The RouterFunction for handling OpenAPI routes.
     */
    @Bean
    fun openAPIRoute(
        openAPIProperties: OpenAPIProperties,
        errorConstructor: OpenDocErrorConstructor,
    ): RouterFunction<ServerResponse> {
        return RouterFunctions
            .route()
            .GET(openAPIProperties.apiWebPath) { rq ->
                return@GET getFileBody(rq.pathVariable("fileName"), openAPIProperties, rq, errorConstructor)
            }.build()
    }

    protected open fun getFileBody(
        fileName: String,
        openAPIProperties: OpenAPIProperties,
        rq: ServerRequest,
        errorConstructor: OpenDocErrorConstructor,
    ): ServerResponse {
        try {
            if (!allowedFileRegex.matches(fileName)) {
                throw RuntimeException("Bad filename")
            }
            val resourceToByteArray =
                IOUtils.resourceToByteArray("/openapi/$fileName").let { array ->
                    if (fileName.equals("swagger-initializer.js", true)) {
                        String(array).replace("@ApiPathName@", openAPIProperties.filePath).toByteArray()
                    } else {
                        array
                    }
                }
            val contentType = getContentType(fileName)
            val builder = ServerResponse.ok().header(HttpHeaders.CONTENT_TYPE, contentType)
            if (contentType == MediaType.APPLICATION_OCTET_STREAM_VALUE) {
                builder.header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"${fileName}\"")
            }
            val body = builder.body(resourceToByteArray)
            rq.logResponseBody(false)
            return body
        } catch (t: Throwable) {
            logger.log(Level.WARNING, "Error in openapi controller", t)
            return errorConstructor.constructError(t)
        }
    }

    protected open fun getContentType(fileName: String) =
        if (fileName.endsWith("html", true)) {
            MediaType.TEXT_HTML_VALUE
        } else if (fileName.endsWith("js", true)) {
            "application/javascript; charset=utf-8"
        } else if (fileName.endsWith("css", true)) {
            "text/css"
        } else if (fileName.endsWith("png", true)) {
            MediaType.IMAGE_PNG_VALUE
        } else if (fileName.endsWith("json", true)) {
            MediaType.APPLICATION_JSON_VALUE
        } else if (fileName.endsWith("yml", true)) {
            MediaType.TEXT_PLAIN_VALUE
        } else if (fileName.endsWith("yaml", true)) {
            MediaType.TEXT_PLAIN_VALUE
        } else {
            MediaType.APPLICATION_OCTET_STREAM_VALUE
        }
}

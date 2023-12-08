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

package com.alcosi.lib.doc

import com.alcosi.lib.dto.APIError
import org.apache.commons.io.IOUtils
import org.springframework.beans.factory.annotation.Value
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.http.HttpHeaders
import org.springframework.http.MediaType
import org.springframework.web.servlet.function.RouterFunction
import org.springframework.web.servlet.function.RouterFunctions
import org.springframework.web.servlet.function.ServerResponse
import java.util.logging.Level
import java.util.logging.Logger

@Configuration
@ConditionalOnProperty(
    prefix = "common-lib.springdoc",
    name = arrayOf("disabled"),
    matchIfMissing = true,
    havingValue = "false"
)
open class OpenAPIConfig {
    val allowedFileRegex: Regex = "^([a-zA-Z0-9_\\-()])+(\\.\\w{1,4})\$".toRegex()
    val logger = Logger.getLogger(this.javaClass.name)

    @Bean
    @ConditionalOnMissingBean(OpenDocErrorConstructor::class)
    open fun openApiErrorConstructor(): OpenDocErrorConstructor.Default {
        return OpenDocErrorConstructor.Default()
    }

    @Bean
    open fun openAPIRoute(
        @Value("\${common-lib.openapi.url:/openapi/{fileName}}") apiWebPath: String,
        @Value("\${common-lib.openapi.file-path:openapi.yaml}") apiFilePath: String,
        errorConstructor: OpenDocErrorConstructor
    ): RouterFunction<ServerResponse> {
        return RouterFunctions
            .route()
            .GET(apiWebPath) {
                val fileName = it.pathVariable("fileName")
                val rs = try {
                    if (!allowedFileRegex.matches(fileName)) {
                        throw RuntimeException("Bad filename")
                    }
                    val resourceToByteArray = IOUtils.resourceToByteArray("/openapi/$fileName").let {array->
                        if (fileName.equals("swagger-initializer.js",true)){
                            String(array).replace("@ApiPathName@",apiFilePath).toByteArray()
                        } else{
                            array
                        }
                    }
                    val type =
                        if (fileName.endsWith("html", true)) MediaType.TEXT_HTML_VALUE
                        else if (fileName.endsWith("js", true)) "application/javascript; charset=utf-8"
                        else if (fileName.endsWith("css", true)) "text/css"
                        else if (fileName.endsWith("png", true)) MediaType.IMAGE_PNG_VALUE
                        else if (fileName.endsWith("json", true)) MediaType.APPLICATION_JSON_VALUE
                        else if (fileName.endsWith("yml", true)) MediaType.TEXT_PLAIN_VALUE
                        else if (fileName.endsWith("yaml", true)) MediaType.TEXT_PLAIN_VALUE
                        else MediaType.APPLICATION_OCTET_STREAM_VALUE

                    val builder = ServerResponse.ok()
                        .header(HttpHeaders.CONTENT_TYPE, type);
                    if (type == MediaType.APPLICATION_OCTET_STREAM_VALUE) {
                        builder.header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"${fileName}\"")
                    }
                    builder.body(resourceToByteArray)
                } catch (t: Throwable) {
                    logger.log(Level.WARNING, "Error in openapi controller", t)
                    errorConstructor.constructError(t)
                }
                return@GET rs
            }
            .build()
    }


}
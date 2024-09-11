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
import com.alcosi.lib.logging.http.AlcosiHttpLoggingHelper
import io.github.breninsul.logging.HttpLoggingHelper
import io.github.breninsul.okhttp.logging.OkHttpLoggerProperties
import io.github.breninsul.okhttp.logging.OkHttpRequestBodyMasking
import io.github.breninsul.okhttp.logging.OkHttpResponseBodyMasking
import io.github.breninsul.okhttp.logging.OkHttpUriMasking
import okhttp3.*
import java.util.*

/**
 * OKLoggingInterceptor is an open class that implements the Interceptor interface and Ordered interface.
 * It intercepts the HTTP requests and logs the request and response information.
 *
 * @property maxBodySize The maximum size of the response body to log.
 * @property loggingLevel The logging level for the interceptor.
 * @property order The order of the interceptor in the chain.
 */
open class OKLoggingInterceptor(
    properties: OkHttpLoggerProperties,
    uriMasking: List<OkHttpUriMasking>,
    requestBodyMaskers: List<OkHttpRequestBodyMasking>,
    responseBodyMaskers: List<OkHttpResponseBodyMasking>,
    protected open val headerHelper: HeaderHelper,
) : io.github.breninsul.okhttp.logging.OKLoggingInterceptor(properties, uriMasking, requestBodyMaskers, responseBodyMaskers) {
    override val helper: HttpLoggingHelper = AlcosiHttpLoggingHelper({ headerHelper.getContextRqId() }, "OkHTTP", properties, uriMasking, requestBodyMaskers, responseBodyMaskers)
}

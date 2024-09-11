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

package com.alcosi.lib.filters.servlet.log

import com.alcosi.lib.filters.servlet.ThreadContext
import com.alcosi.lib.logging.http.AlcosiHttpLoggingHelper
import io.github.breninsul.logging.HttpLoggingHelper
import io.github.breninsul.servlet.logging.*

/**
 * Performs logging for servlet requests and responses.
 *
 * @param properties The configuration properties for the logging filter.
 * @param requestBodyMaskers The list of request body maskers to be applied.
 * @param responseBodyMaskers The list of response body maskers to be applied.
 * @param threadContext The thread context used to retrieve the request ID.
 */
open class LoggingFilter(
    properties: ServletLoggerProperties,
    uriMasking: List<ServletUriMasking>,
    requestBodyMaskers: List<ServletRequestBodyMasking>,
    responseBodyMaskers: List<ServletResponseBodyMasking>,
    threadContext: ThreadContext,
) : ServletLoggingFilter(properties, uriMasking, requestBodyMaskers, responseBodyMaskers) {
    val id = threadContext.getRqId()
    override val helper: HttpLoggingHelper = AlcosiHttpLoggingHelper({ threadContext.getRqId() }, "Servlet", properties.toHttpLoggingProperties(), uriMasking, requestBodyMaskers, responseBodyMaskers)
}

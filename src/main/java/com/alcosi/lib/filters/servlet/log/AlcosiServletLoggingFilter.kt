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
import com.alcosi.lib.logging.http.AlcosiHttpLogging2Helper
import io.github.breninsul.logging2.HttpLoggingHelper
import io.github.breninsul.servlet.logging2.*
import io.github.breninsul.servlet.logging2.filter.ServletLoggerService
import io.github.breninsul.servlet.logging2.filter.ServletLoggingFilter
import org.springframework.web.servlet.HandlerMapping

/**
 * LoggingFilter is an extension of the ServletLoggingFilter that provides advanced logging capabilities for incoming requests.
 * It adds support for request identification via a thread-local request ID, enhanced logging utilities, and URI masking.
 *
 * The filter integrates with several components to facilitate its functionality:
 *
 * - `ServletLoggerService`: Responsible for logging servlet-based events.
 * - `ServletLoggerProperties`: Configuration properties for logging behavior.
 * - `ServletUriMasking`: A list of URI masking rules applied to sensitive parameters in logged URIs.
 * - `HandlerMapping`: Used for identifying the handler mappings to associate log events with specific handlers.
 * - `ThreadContext`: A context-per-thread abstraction allowing the storage and retrieval of localized information, such as request IDs.
 *
 * This filter supports seamless identification of a unique request through a request ID, fetched or generated using `ThreadContext`.
 * Additionally, it uses `HttpLoggingHelper` to provide request-specific logging enhancements, enabling better tracking and observability.
 *
 * The `LoggingFilter` can be used in environments requiring detailed logging with configurable masking of sensitive data, and unique identification of requests for correlation across
 *  services.
 */
open class AlcosiServletLoggingFilter(
    servletLoggerService: AlcosiServletLoggerService,
    properties: ServletLoggerProperties,
    handlerMappings: List<HandlerMapping>,
) : ServletLoggingFilter(servletLoggerService,properties,handlerMappings) {

}

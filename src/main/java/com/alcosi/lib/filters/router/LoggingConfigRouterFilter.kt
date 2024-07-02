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

import com.alcosi.lib.filters.servlet.log.ServletLoggingFilterConfig
import com.alcosi.lib.filters.servlet.log.ServletLoggingFilterConfig.Companion.LOG_CONFIG_ATTRIBUTE
import org.springframework.web.servlet.function.HandlerFunction
import org.springframework.web.servlet.function.ServerRequest
import org.springframework.web.servlet.function.ServerResponse

open class LoggingConfigRouterFilter(
    protected open val config: ServletLoggingFilterConfig,
) : RouterFilter {
    override fun filter(
        request: ServerRequest,
        next: HandlerFunction<ServerResponse>,
    ): ServerResponse {
        request.servletRequest().setAttribute(LOG_CONFIG_ATTRIBUTE, config)
        return next.handle(request)
    }

    override fun getOrder() = -1
}

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

import com.alcosi.lib.filters.servlet.log.ServletLoggingFilterConfig.Companion.LOG_CONFIG_ATTRIBUTE
import jakarta.servlet.FilterChain
import jakarta.servlet.http.HttpServletRequest
import jakarta.servlet.http.HttpServletResponse
import org.springframework.boot.web.servlet.FilterRegistrationBean
import org.springframework.web.filter.OncePerRequestFilter

open class LoggingConfigFilter(
    protected open val config: ServletLoggingFilterConfig,
) : OncePerRequestFilter() {
    override fun doFilterInternal(
        request: HttpServletRequest,
        response: HttpServletResponse,
        filterChain: FilterChain,
    ) {
        request.setAttribute(LOG_CONFIG_ATTRIBUTE, config)
    }

    open class RegistrationBean(
        protected open val config: ServletLoggingFilterConfig,
        urlPatterns: List<String>,
    ) : FilterRegistrationBean<LoggingConfigFilter>() {
        init {
            this.filter = LoggingConfigFilter(config)
            this.order = Int.MIN_VALUE
            this.setUrlPatterns(urlPatterns)
        }
    }
}

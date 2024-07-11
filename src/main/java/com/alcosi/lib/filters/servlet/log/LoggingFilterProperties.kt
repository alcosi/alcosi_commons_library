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

import io.github.breninsul.logging.HttpLogSettings
import io.github.breninsul.logging.JavaLoggingLevel
import io.github.breninsul.servlet.logging.ServletHttpRequestLogSettings
import io.github.breninsul.servlet.logging.ServletLoggerProperties
import org.springframework.boot.context.properties.ConfigurationProperties

@ConfigurationProperties("common-lib.filter.logging")
open class LoggingFilterProperties(
    enabled: Boolean = true,
    loggingLevel: JavaLoggingLevel = JavaLoggingLevel.INFO,
    request: ServletHttpRequestLogSettings = ServletHttpRequestLogSettings(tookTimeIncluded = false),
    response: HttpLogSettings = HttpLogSettings(),
    open var orderDelta: Int = 2,
    newLineColumnSymbols: Int = 14,
) : ServletLoggerProperties(enabled, loggingLevel, request, response, orderDelta, newLineColumnSymbols)

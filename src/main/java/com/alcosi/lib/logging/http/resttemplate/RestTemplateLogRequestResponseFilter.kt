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

package com.alcosi.lib.logging.http.resttemplate

import com.alcosi.lib.filters.servlet.HeaderHelper
import io.github.breninsul.rest.logging.RestTemplateLoggerProperties
import io.github.breninsul.rest.logging.RestTemplateLoggingInterceptor
import io.github.breninsul.rest.logging.RestTemplateRequestBodyMasking
import io.github.breninsul.rest.logging.RestTemplateResponseBodyMasking
import java.util.*

/**
 * The `RestTemplateLogRequestResponseFilter` class is a subclass of `RestTemplateLoggingInterceptor` and is responsible
 * for logging the request and response details of a REST API call made using the `RestTemplate` class.
 *
 * @param properties The properties used to configure the filter.
 * @param requestBodyMaskers The list of `RestTemplateRequestBodyMasking` instances used to mask request body fields.
 * @param responseBodyMaskers The list of `RestTemplateResponseBodyMasking` instances used to mask response body fields.
 * @param headerHelper The `HeaderHelper` instance used by the filter.
 *
 * @see RestTemplate
 */
open class RestTemplateLogRequestResponseFilter(
    properties: RestTemplateLoggerProperties,
    requestBodyMaskers: List<RestTemplateRequestBodyMasking>,
    responseBodyMaskers: List<RestTemplateResponseBodyMasking>,
    protected open val headerHelper: HeaderHelper,
) : RestTemplateLoggingInterceptor(properties, requestBodyMaskers, responseBodyMaskers) {
    override fun getIdString(): String = headerHelper.getContextRqId()
}

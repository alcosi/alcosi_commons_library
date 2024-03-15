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

package com.alcosi.lib.exception

import org.springframework.http.HttpStatusCode
import org.springframework.web.server.ResponseStatusException

open class ApiException(
    val code: Long,
    messageOriginal: String,
    val httpCode: Int =
        code.toString().substring(
            0,
            3,
        ).toInt(),
) : ResponseStatusException(HttpStatusCode.valueOf(httpCode), messageOriginal)

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

import com.alcosi.lib.dto.APIError
import org.springframework.web.servlet.function.ServerResponse

/**
 * Represents an interface for constructing error responses in an OpenDoc system.
 * Implementing classes are responsible for constructing the error response based on the given throwable object.
 */
interface OpenDocErrorConstructor {
    /**
     * Constructs an error response based on the given throwable.
     *
     * @param t The throwable.
     * @return The constructed server response.
     */
    fun constructError(t: Throwable): ServerResponse

    /**
     * Default Class
     *
     * This class is responsible for constructing an error response
     * when an exception occurs.
     */
    open class Default : OpenDocErrorConstructor {
        /**
         * Constructs an error response based on the given throwable.
         *
         * @param t The throwable object that caused the error.
         * @return The constructed server response representing the error.
         */
        override fun constructError(t: Throwable): ServerResponse {
            val error = APIError(t.message ?: "", 404000, t.javaClass.simpleName)
            return ServerResponse.status(error.httpCode).body(error)
        }
    }
}

/*
 * Copyright (c) 2024  Alcosi Group Ltd. and affiliates.
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

package com.alcosi.lib.filters.servlet

import com.alcosi.lib.filters.servlet.context.ContextFilter
import com.alcosi.lib.security.ClientAccountDetails
import com.alcosi.lib.security.UserDetails
import org.springframework.http.HttpHeaders
import java.util.concurrent.atomic.AtomicInteger

open class HeaderHelper(
    val serviceName: String,
    val environment: String,
    protected val threadContext: ThreadContext,
) {
    open val contextHeaders = listOf(CLIENT_ID, ACCOUNT_ID, USER_ID, RQ_ID, ACTION)
    open val jsonHeaders =
        listOf(
            ContextFilter.JsonHeader(USER_DETAILS, UserDetails::class, AUTH_PRINCIPAL),
            ContextFilter.JsonHeader(ACCOUNT_DETAILS, ClientAccountDetails::class, AUTH_PRINCIPAL),
        )

    open fun createRequestHeadersMap(): Map<String, String> {
        val headers = mutableMapOf<String, String?>()
        headers[ENV_HEADER] = environment
        headers[SERVICE_NAME] = serviceName
        headers[CLIENT_ID] = threadContext.get(CLIENT_ID)
        headers[ACCOUNT_ID] = threadContext.get(ACCOUNT_ID)
        headers[USER_ID] = threadContext.get(USER_ID)
        headers[RQ_ID] = "${getContextRqId()}-${getRqIdIndex()}"
        return headers.filter { it.value != null } as Map<String, String>
    }

    open fun createRequestHeaders(): HttpHeaders {
        val headers = HttpHeaders()
        createRequestHeadersMap().forEach {
            headers[it.key] = it.value
        }
        return headers
    }

    open fun getContextRqId(): String {
        return threadContext.getRqId()
    }

    protected open fun getRqIdIndex(): Int {
        return if (threadContext.contains("RQ_ID_INDEX")) {
            threadContext.get<AtomicInteger>("RQ_ID_INDEX")!!.incrementAndGet()
        } else {
            val firstIndex = 1
            threadContext.set("RQ_ID_INDEX", AtomicInteger(firstIndex))
            firstIndex
        }
    }

    companion object {
        val KEY_PROVIDER_AUTH_HEADER = "KEY_PROVIDER_ACCESS_KEY"
        val SERVICE_AUTH_HEADER = "AUTHORIZATION"
        val ENV_HEADER = "ENVIRONMENT"
        val SERVICE_NAME = "SERVICE_NAME"
        val CLIENT_ID = "CLIENT_ID"
        val ACCOUNT_ID = "ACCOUNT_ID"
        val USER_ID = "USER_ID"
        val USER_DETAILS = "USER_DETAILS"
        val ACCOUNT_DETAILS = "ACCOUNT_DETAILS"
        val RQ_ID = "RQ_ID"
        val ACTION = "ACTION"
        val AUTH_PRINCIPAL = "AUTH_PRINCIPAL"
    }
}

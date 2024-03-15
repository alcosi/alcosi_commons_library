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

package com.alcosi.lib.filters.servlet

import com.alcosi.lib.filters.servlet.ThreadContext.Companion.AUTH_PRINCIPAL
import com.alcosi.lib.filters.servlet.ThreadContext.Companion.RQ_ID_INDEX
import com.alcosi.lib.filters.servlet.context.ContextFilter
import com.alcosi.lib.security.AccountDetails
import com.alcosi.lib.security.UserDetails
import org.springframework.http.HttpHeaders
import java.util.concurrent.atomic.AtomicInteger

open class HeaderHelper(
    val serviceName: String,
    val environment: String,
    protected val threadContext: ThreadContext,
) {
    open val contextHeaders = listOf(ACCOUNT_ID, USER_ID, RQ_ID, ACTION)
    open val jsonHeaders =
        listOf(
            ContextFilter.JsonHeader(USER_DETAILS, UserDetails::class, AUTH_PRINCIPAL),
            ContextFilter.JsonHeader(ACCOUNT_DETAILS, AccountDetails::class, AUTH_PRINCIPAL),
        )

    open fun createRequestHeadersMap(): Map<String, String> {
        val headers = mutableMapOf<String, String?>()
        headers[ENV_HEADER] = environment
        headers[SERVICE_NAME] = serviceName
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
        return if (threadContext.contains(RQ_ID_INDEX)) {
            threadContext.get<AtomicInteger>(RQ_ID_INDEX)!!.incrementAndGet()
        } else {
            val firstIndex = 1
            threadContext.set(RQ_ID_INDEX, AtomicInteger(firstIndex))
            firstIndex
        }
    }

    companion object {
        val KEY_PROVIDER_AUTH_HEADER = "KEY_PROVIDER_ACCESS_KEY"
        val SERVICE_AUTH_HEADER = "AUTHORIZATION"
        val ENV_HEADER = "ENVIRONMENT"
        val SERVICE_NAME = "SERVICE_NAME"
        val ACCOUNT_ID = "ACCOUNT_ID"
        val USER_ID = "USER_ID"
        val USER_DETAILS = "USER_DETAILS"
        val ACCOUNT_DETAILS = "ACCOUNT_DETAILS"
        val ORIGINAL_AUTHORISATION = "ORIGINAL_AUTHORISATION"
        val RQ_ID = "RQ_ID"
        val ACTION = "ACTION"
    }
}

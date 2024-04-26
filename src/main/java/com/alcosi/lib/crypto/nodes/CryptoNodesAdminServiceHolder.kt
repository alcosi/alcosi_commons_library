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

package com.alcosi.lib.crypto.nodes

import org.web3j.protocol.admin.Admin
import java.util.*

open class CryptoNodesAdminServiceHolder(adminMap: Map<Int, Admin>?) {
    val adminMap: Map<Int, Admin>

    init {
        this.adminMap = Collections.unmodifiableMap(adminMap)
    }

    operator fun get(chainId: Int): Admin {
        check(adminMap.containsKey(chainId)) { "Network $chainId is not configured" }
        return adminMap[chainId]!!
    }

    open fun networks(): Set<Int> {
        return adminMap.keys
    }
}

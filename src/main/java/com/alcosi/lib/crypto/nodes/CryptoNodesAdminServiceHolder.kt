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

/**
 * CryptoNodesAdminServiceHolder is a class that holds the admin services' configuration for Crypto Nodes.
 * It provides methods to retrieve admin services and network chain IDs.
 *
 * @param adminMap The map of admin services configured for Crypto Nodes. The key of the map is the chain ID
 *        of the network, and the value is an instance of the Admin class.
 */
open class CryptoNodesAdminServiceHolder(adminMap: Map<Int, Admin>?) {
    /**
     * adminMap is a read-only property of type Map<Int, Admin>.
     * It represents the map of admin services configured for Crypto Nodes,
     * where the key of the map is the chain ID of the network, and the value is an instance of the Admin class.
     *
     * Usage Examples:
     *
     * 1. Get the Admin instance for a specific chain ID:
     *    ```
     *    val admin: Admin = adminMap[chainId]
     *    ```
     *
     *    This retrieves the Admin instance associated with the specified chain ID.
     *    If the network with the given chain ID is not configured, a NoSuchElementException is thrown.
     *
     * 2. Get the set of network chain IDs configured in the adminMap:
     *    ```
     *    val chainIds: Set<Int> = adminMap.keys
     *    ```
     *
     *    This returns a set of network chain IDs, representing all the networks configured in the adminMap.
     */
    val adminMap: Map<Int, Admin>

    init {
        this.adminMap = Collections.unmodifiableMap(adminMap)
    }

    /**
     * Retrieves the Admin instance for the specified chain ID.
     *
     * @param chainId The chain ID of the network.
     * @return The Admin instance for the specified chain ID.
     * @throws NoSuchElementException if the network with the given chain ID is not configured.
     */
    operator fun get(chainId: Int): Admin {
        check(adminMap.containsKey(chainId)) { "Network $chainId is not configured" }
        return adminMap[chainId]!!
    }

    /**
     * Retrieves the set of network chain IDs configured in this CryptoNodesAdminServiceHolder.
     *
     * @return The set of network chain IDs as a Set of Int.
     */
    open fun networks(): Set<Int> {
        return adminMap.keys
    }
}

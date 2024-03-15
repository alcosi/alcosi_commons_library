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

package com.alcosi.lib.logging.http

import org.springframework.core.Ordered
import org.springframework.core.PriorityOrdered

object OrderedComparator : Comparator<Any?> {
    override fun compare(
        a: Any?,
        b: Any?,
    ): Int {
        if (a == null) {
            return if (b == null) 0 else -1
        }
        if (b == null)
            {
                return 1
            }
        val priorityOrderedCompare =
            when (a) {
                is PriorityOrdered -> if (b is PriorityOrdered) a.order.compareTo(b.order) else 1
                else -> if (b is PriorityOrdered) -1 else 0
            }
        if (priorityOrderedCompare != 0) {
            return priorityOrderedCompare
        }
        val orderedCompare =
            when (a) {
                is Ordered -> if (b is Ordered) a.order.compareTo(b.order) else 1
                else -> if (b is Ordered) -1 else 0
            }
        if (orderedCompare != 0) {
            return orderedCompare
        }
        return a.javaClass.simpleName.compareTo(b.javaClass.simpleName)
    }
}

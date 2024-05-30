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

/**
 * A comparator that orders objects based on their priority and order values.
 *
 * This comparator is used to sort objects that implement the [PriorityOrdered] or [Ordered] interface.
 * Objects that implement the [PriorityOrdered] interface are ordered based on their [PriorityOrdered.order] value,
 * with lower values indicating higher priority. If two objects have the same [PriorityOrdered.order] value,
 * then the comparison falls back to the [Ordered] interface, where objects are ordered based on their [Ordered.order] value.
 * If an object does not implement either interface, it is considered to have the lowest priority.
 *
 * If two objects have the same highest priority, the comparison falls back to the simple class name comparison.
 *
 * @see PriorityOrdered
 * @see Ordered
 *
 * Usage Example:
 *
 **/
object OrderedComparator : Comparator<Any?> {
    /**
     * Compares two objects based on their priority, order, and class name.
     *
     * @param a The first object to be compared.
     * @param b The second object to be compared.
     * @return Zero if the objects are equal. A negative value if `a` is less than `b`,
     *    or a positive value if `a` is greater than `b`.
     */
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

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

package com.alcosi.lib.executors

import java.util.function.Supplier

/**
 * A class that extends the InheritableThreadLocal class to provide preset initial values for thread-local variables.
 *
 * @param[T] The type of the thread-local variable.
 * @property[initialValSupplier] The supplier function that provides the initial value for the thread-local variable.
 * @constructor Creates a PresetInheritableThreadLocal instance with the given initial value supplier.
 */
open class PresetInheritableThreadLocal<T>(protected val initialValSupplier: Supplier<T>) : InheritableThreadLocal<T>() {
    constructor(initialVal: T) : this(Supplier { initialVal })

    /**
     * Returns the initial value for the InheritableThreadLocal variable.
     *
     * @return the initial value for the InheritableThreadLocal variable.
     */
    override fun initialValue(): T {
        return initialValSupplier.get()
    }

    /**
     * Removes the current value of the InheritableThreadLocal variable and sets it to the initial value.
     */
    override fun remove() {
        super.remove()
        set(initialValue())
    }
}

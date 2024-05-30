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

package com.alcosi.lib.synchronisation

import java.util.concurrent.Callable

/**
 * SynchronizationService class manages synchronization locks for tasks.
 *
 * This class provides functionality to execute tasks in a synchronized manner
 * using locks. It wraps a delegate SynchronizationService provided by the
 * `io.github.breninsul.synchronizationstarter.service` package.
 *
 * @property delegate The delegate SynchronizationService object.
 *
 * @constructor Creates an instance of SynchronizationService with the specified delegate.
 *
 * @param delegate The*/
@Deprecated("Use io.github.breninsul.synchronizationstarter.service.SynchronizationService", replaceWith = ReplaceWith("io.github.breninsul.synchronizationstarter.service.SynchronizationService","io.github.breninsul.synchronizationstarter.service.SynchronizationService"))
open class SynchronizationService(protected val delegate: io.github.breninsul.synchronizationstarter.service.SynchronizationService) {
    /**
     * Executes the synchronization logic before performing a task.
     *
     * This method checks if the provided `id` is not null. If `id` is null, it returns false.
     * Otherwise, it delegates the execution to the `before` method of the `delegate SynchronizationService` object.
     *
     * @param id The identifier of the task to be synchronized. This can be of any type.
     * @return Returns a boolean value indicating whether the synchronization was successful.
     */
    open fun before(id: Any?): Boolean {
        if (id == null) {
            return false
        }
        return delegate.before(id)
    }

    /**
     * Executes the `after` operation on the delegated SynchronizationService object.
     *
     * This method is called after the completion of a task execution to release the synchronization lock.
     * It takes an `id` parameter which represents the identifier of the lock associated with the task.
     *
     * If the `id` parameter is `null`, the method does nothing and returns immediately.
     * Otherwise, it calls the `after` method on the delegate SynchronizationService object passing the `id` parameter.
     *
     * @param id The identifier of the lock associated with the task.
     */
    open fun after(id: Any?) {
        if (id == null) {
            return
        }
        return delegate.after(id)
    }
    /**
     * Executes a task synchronously with the specified id.
     *
     * This method executes the given task in a synchronized manner by acquiring a lock
     * identified by the specified id. The task is executed by calling its `call()` method
     * from the `java.util.concurrent.Callable` interface.
     *
     * @param id The id of the synchronization lock to acquire.
     * @param task The task to execute.
     * @return The result of the executed task.
     */
    open fun <R> sync(
        id: Any,
        task: Callable<R>,
    ): R {
        before(id)
        try {
            return task.call()
        } finally {
            after(id)
        }
    }

}

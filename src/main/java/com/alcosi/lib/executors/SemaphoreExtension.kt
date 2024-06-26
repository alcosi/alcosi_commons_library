/*
 *
 *  * Copyright (c) 2024 Alcosi Group Ltd. and affiliates.
 *  *
 *  * Licensed under the Apache License, Version 2.0 (the "License");
 *  * you may not use this file except in compliance with the License.
 *  * You may obtain a copy of the License at
 *  *
 *  *     http://www.apache.org/licenses/LICENSE-2.0
 *  *
 *  * Unless required by applicable law or agreed to in writing, software
 *  * distributed under the License is distributed on an "AS IS" BASIS,
 *  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *  * See the License for the specific language governing permissions and
 *  * limitations under the License.
 *  *
 *
 */

package com.alcosi.lib.executors

import java.util.concurrent.Callable
import java.util.concurrent.Semaphore

/**
 * Executes the given [runnable] while acquiring the semaphore lock.
 *
 * @param runnable the task to execute while holding the semaphore lock.
 * @return the result of the [runnable] execution.
 */
fun <T> Semaphore.sync(runnable: Callable<T>): T {
    try {
        this.acquire()
        return runnable.call()
    } finally {
        this.release()
    }
}

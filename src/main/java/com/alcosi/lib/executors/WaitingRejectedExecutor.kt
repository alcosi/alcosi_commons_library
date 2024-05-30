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

import java.util.concurrent.RejectedExecutionHandler
import java.util.concurrent.ThreadPoolExecutor
import java.util.logging.Level
import java.util.logging.Logger

/**
 * This class represents a custom implementation of the [RejectedExecutionHandler] interface.
 * When a task is rejected by a [ThreadPoolExecutor], this class will attempt to put the rejected task back into the executor's queue for later execution.
 * If an error occurs while trying to put the task back into the queue, it will be logged at the [SEVERE] level using the provided logger.
 *
 * @property logger The logger instance used for logging any errors that occur during the execution of the rejected task.
 */
open class WaitingRejectedExecutor : RejectedExecutionHandler {
    /**
     * This method is called when a task is rejected by a [ThreadPoolExecutor].
     * It attempts to put the rejected task back into the executor's queue for later execution.
     * If an error occurs while trying to put the task back into the queue, it logs the error at the SEVERE level using the provided logger.
     *
     * @param r The rejected task that needs to be put back into the executor's queue.
     * @param executor The [ThreadPoolExecutor] that rejected the task.
     */
    override fun rejectedExecution(
        r: Runnable,
        executor: ThreadPoolExecutor,
    ) {
        try {
            executor.queue.put(r)
        } catch (t: Throwable) {
            logger.log(Level.SEVERE, "Error executor $r", t)
        }
    }

    /**
     * This class represents the companion object for the [WaitingRejectedExecutor] class.
     * It provides a logger instance for logging any errors that occur during the execution of a rejected task.
     */
    companion object {
        val logger = Logger.getLogger(this::class.java.name)
    }
}

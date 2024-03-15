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

class WaitingRejectedExecutor : RejectedExecutionHandler {
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

    companion object {
        val logger = Logger.getLogger(this::class.java.name)
    }
}

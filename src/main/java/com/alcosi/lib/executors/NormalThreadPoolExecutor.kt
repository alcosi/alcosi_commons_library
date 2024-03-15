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

import org.apache.commons.lang3.concurrent.BasicThreadFactory
import java.time.Duration
import java.util.concurrent.*
import java.util.logging.Level
import java.util.logging.Logger

open class NormalThreadPoolExecutor protected constructor(
    corePoolSize: Int,
    maximumPoolSize: Int,
    keepAliveTime: Duration,
    workQueue: BlockingQueue<Runnable?>?,
    threadFactory: ThreadFactory?,
    handler: RejectedExecutionHandler?,
) : ThreadPoolExecutor(corePoolSize, maximumPoolSize, keepAliveTime.toMillis(), TimeUnit.MILLISECONDS, workQueue, threadFactory, handler) {
    override fun afterExecute(
        r: Runnable?,
        t: Throwable?,
    ) {
        if (t != null) {
            logger.log(Level.SEVERE, "Exception in thread ${Thread.currentThread().name}.", t)
        } else {
            logger.log(Level.CONFIG, "Tsk ${Thread.currentThread().name} completed ")
        }
        super.afterExecute(r, t)
    }

    companion object {
        val logger = Logger.getLogger(this::class.java.name)
        protected val UNCAUGHT_EXCEPTION_HANDLER = UncaughtExceptionHandler()
        protected val WAITING_REJECTED_EXECUTOR = WaitingRejectedExecutor()

        fun build(
            threads: Int,
            name: String,
            keepAlive: Duration,
        ): NormalThreadPoolExecutor {
            val factory =
                BasicThreadFactory.Builder()
                    .namingPattern("$name-%d")
                    .daemon(false)
                    .uncaughtExceptionHandler(UNCAUGHT_EXCEPTION_HANDLER)
                    .priority(Thread.NORM_PRIORITY)
                    .build()
            return NormalThreadPoolExecutor(
                threads,
                threads,
                keepAlive,
                LinkedBlockingQueue(),
                factory,
                WAITING_REJECTED_EXECUTOR,
            )
        }
    }
}

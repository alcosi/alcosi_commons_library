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

import io.github.breninsul.namedlimitedvirtualthreadexecutor.service.blocking.LimitedBackpressureBlockingExecutor
import java.util.concurrent.ExecutorService
import java.util.logging.Logger

/**
 * NormalThreadPoolExecutor is a class that extends ExecutorService and provides an implementation of a thread pool executor
 * with limited backpressure and blocking behavior.
 *
 * @constructor Creates a NormalThreadPoolExecutor instance with the given delegate.
 * @param delegate The delegate executor service.
 *
 * @property logger The logger for the NormalThreadPoolExecutor class.
 *
 * @see ExecutorService
 */
open class NormalThreadPoolExecutor protected constructor(
    delegate: LimitedBackpressureBlockingExecutor,
) : ExecutorService by delegate {
    /**
     * The Companion object of the NormalThreadPoolExecutor class.
     * It provides a logger and a build() function for creating instances of NormalThreadPoolExecutor.
     */
    companion object {
        val logger = Logger.getLogger(this::class.java.name)

        /**
         * Builds a NormalThreadPoolExecutor instance with the given parameters.
         *
         * @param threads The number of threads in the thread pool.
         * @param name The name of the thread pool.
         * @param inheritThreadLocals Whether to inherit thread locals from the calling thread to the worker threads. Default value is true.
         *
         * @return A NormalThreadPoolExecutor instance.
         */
        fun build(
            threads: Int,
            name: String,
            inheritThreadLocals: Boolean = true
        ): NormalThreadPoolExecutor {
            return NormalThreadPoolExecutor(LimitedBackpressureBlockingExecutor.buildVirtual(name, threads, inheritThreadLocals = inheritThreadLocals))
        }
    }
}

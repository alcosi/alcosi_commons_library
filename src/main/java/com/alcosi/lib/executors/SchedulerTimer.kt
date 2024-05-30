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

import io.github.breninsul.javatimerscheduler.registry.SchedulerType
import io.github.breninsul.javatimerscheduler.registry.TaskSchedulerRegistry
import java.time.Duration
import java.util.logging.Level

/**
 * The base abstract class for a scheduler timer.
 *
 * @property delay The delay between each batch of tasks.
 * @property name The name of the scheduler timer.
 * @property loggingLevel The logging level for the scheduler timer.
 * @property firstDelay The delay before the first batch of tasks.
 */
@Deprecated("Use TaskSchedulerRegistry or SpringDynamicScheduleRegistry", replaceWith = ReplaceWith("Use TaskSchedulerRegistry or SpringDynamicScheduleRegistry","io.github.breninsul.javatimerscheduler.registry.SpringDynamicScheduleRegistry","io.github.breninsul.javatimerscheduler.autoconfigure.SpringDynamicScheduleRegistry"))
abstract class SchedulerTimer(
    val delay: Duration,
    val name: String = "SchedulerTimer",
    val loggingLevel: Level = Level.FINE,
    val firstDelay: Duration = delay,
) {
    /**
     * This method is called to start a batch of tasks.
     * It is an abstract method, meaning it must be implemented by a subclass.
     */
    abstract fun startBatch()

    init {
        /**
         * Register task for startBatch function
         */
        TaskSchedulerRegistry.registerTypeTask(SchedulerType.VIRTUAL_WAIT, name, delay, firstDelay, this::class, loggingLevel, runnable = Runnable { startBatch() })
    }
}

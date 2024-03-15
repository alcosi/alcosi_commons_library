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

import java.time.Duration
import java.util.*
import java.util.concurrent.atomic.AtomicLong
import java.util.logging.Level
import java.util.logging.Logger

abstract class SchedulerTimer(
    val delay: Duration,
    val name: String = "SchedulerTimer",
    val loggingLevel: Level = Level.FINE,
    val firstDelay: Duration = delay,
) {
    val logger = Logger.getLogger(this.javaClass.name)
    val batchTimer: Timer = createTimer()
    val counter = AtomicLong(1)

    abstract fun startBatch()

    fun createTimer(): Timer {
        val task =
            object : TimerTask() {
                override fun run() {
                    val time = System.currentTimeMillis()
                    val errorText =
                        try {
                            startBatch()
                            ""
                        } catch (t: Throwable) {
                            " ${t.javaClass}:${t.message}"
                        }
                    logger.log(loggingLevel, "$name job №${counter.getAndIncrement()} took ${System.currentTimeMillis() - time}ms.$errorText")
                }
            }
        val timer = Timer(name)
        timer.scheduleAtFixedRate(task, firstDelay.toMillis(), delay.toMillis())
        return timer
    }
}

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

import java.util.logging.Level
import java.util.logging.Logger

/**
 * A class that implements the Thread.UncaughtExceptionHandler interface to handle uncaught exceptions
 * in threads.
 */
open class UncaughtExceptionHandler : Thread.UncaughtExceptionHandler {
    /**
     * Handles uncaught exceptions in threads.
     *
     * @param t The thread that caused the exception.
     * @param e The uncaught exception that occurred.
     */
    override fun uncaughtException(
        t: Thread?,
        e: Throwable?,
    ) {
        logger.log(Level.SEVERE, "Error in thread $t: ", e)
    }

    /**
     * The Companion class contains a logger object for logging information and errors.
     *
     * @property logger The logger object used for logging.
     */
    companion object {
        val logger = Logger.getLogger(this::class.java.name)
    }
}

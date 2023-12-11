/*
 * Copyright (c) 2024  Alcosi Group Ltd. and affiliates.
 *
 * Portions of this software are licensed as follows:
 *
 *     All content that resides under the "alcosi" and "atomicon" or “deploy” directories of this repository, if that directory exists, is licensed under the license defined in "LICENSE.TXT".
 *
 *     All third-party components incorporated into this software are licensed under the original license provided by the owner of the applicable component.
 *
 *     Content outside of the above-mentioned directories or restrictions above is available under the MIT license as defined below.
 *
 *
 *
 * MIT License
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy of this software and associated documentation files (the "Software"), to deal in the Software without restriction, including without limitation the rights to use, copy, modify, merge, publish, distribute, sublicense, and/or sell copies of the Software, and to permit persons to whom the Software is urnished to do so, subject to the following conditions:
 *
 *
 *
 * The above copyright notice and this permission notice shall be included in all copies or substantial portions of the Software.
 *
 *
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY, FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM, OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE SOFTWARE.
 */

package com.alcosi.lib.synchronisation

import com.alcosi.lib.executors.SchedulerTimer
import java.time.Duration
import java.time.LocalDateTime
import java.time.ZoneOffset
import java.util.concurrent.ConcurrentHashMap
import java.util.concurrent.ConcurrentMap
import java.util.concurrent.locks.StampedLock
import java.util.logging.Level
import java.util.logging.Logger
import java.util.stream.Collectors

open class SynchronizationService(val lockLifetime: Duration, val clearDelay: Duration) {
    class ClientLock(val lock: StampedLock = StampedLock(), val createdAt: LocalDateTime = LocalDateTime.now()) {
        @Volatile
        var stamp: Long? = null

        override fun toString(): String {
            return "ClientLock(lock=$lock, createdAt=$createdAt, stamp=$stamp)"
        }

        override fun equals(other: Any?): Boolean {
            if (this === other) return true
            if (javaClass != other?.javaClass) return false

            other as ClientLock

            if (lock != other.lock) return false
            if (createdAt != other.createdAt) return false
            if (stamp != other.stamp) return false

            return true
        }

        override fun hashCode(): Int {
            var result = lock.hashCode()
            result = 31 * result + createdAt.hashCode()
            result = 31 * result + (stamp?.hashCode() ?: 0)
            return result
        }
    }

    protected val locks: ConcurrentMap<Any, ClientLock> = ConcurrentHashMap()

    open fun before(id: Any?): Boolean {
        if (id == null) {
            return false
        }
        logger.log(Level.FINEST, "Lock for $id set")
        val time = System.currentTimeMillis()
        val lock = getLock(id)
        val locked: Boolean = lock.lock.isWriteLocked
        val stamp: Long = lock.lock.writeLock()
        lock.stamp = (stamp)
        val tookMs = System.currentTimeMillis() - time
        logger.log(Level.FINEST, "Lock $id is locked : $locked .Lock Took $tookMs ms")
        if (tookMs > 100) {
            logger.log(Level.SEVERE, "Lock took more then 100ms $tookMs $id")
        }
        return locked
    }

    protected open fun getLock(id: Any): ClientLock {
        val clientLock = locks[id]
        return if (clientLock == null) {
            val lock = ClientLock()
            locks[id] = lock
            lock
        } else {
            clientLock
        }
    }

    open fun after(id: Any?) {
        if (id == null) {
            return
        }
        val clientLock = locks[id]
        if (clientLock == null) {
            logger.log(Level.SEVERE, "No lock for  $id")
        } else {
            logger.log(Level.FINEST, "Lock for $id released")
            unlock(clientLock)
        }
    }

    protected open val scheduler =
        object : SchedulerTimer(clearDelay, "ClearDeadLockedSynchronisation", Level.FINEST) {
            override fun startBatch() {
                val now = LocalDateTime.now()
                val old =
                    locks.entries
                        .filter { (_, value): Map.Entry<Any, ClientLock> ->
                            value
                                .createdAt
                                .plus(lockLifetime)
                                .isBefore(now)
                        }
                old.map { e -> locks[e.key] to e.key }
                    .filter { (it.first?.lock?.isWriteLocked) ?: false }
                    .forEach { l ->
                        Companion.logger.log(Level.SEVERE, "Lock has been blocked before clear :${l.second}.Took ${System.currentTimeMillis() - l.first?.createdAt?.toEpochSecond(ZoneOffset.UTC)!!}ms ")
                        l.first!!.lock.unlockWrite(l.first!!.stamp!!)
                    }
                val removed =
                    old
                        .stream()
                        .map { e -> e.key to locks.remove(e.key) }
                        .map { p -> "${p.first}:${p.second}" }
                        .collect(Collectors.joining(";"))
                if (removed.isNotBlank()) {
                    Companion.logger.log(Level.FINEST, "Lock for $removed removed ")
                }
            }
        }

    protected open fun unlock(l: ClientLock) {
        try {
            val lock: StampedLock = l.lock
            lock.unlockWrite(l.stamp!!)
        } catch (t: Throwable) {
            logger.log(Level.WARNING, "Error unlocking lock ! ${t.javaClass}:${t.message}", t)
        }
    }

    companion object {
        val logger = Logger.getLogger(this::class.java.name)
    }
}

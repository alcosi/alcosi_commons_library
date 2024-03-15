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

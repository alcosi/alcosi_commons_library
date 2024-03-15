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

package com.alcosi.lib.utils

import java.io.FileInputStream
import java.net.URLClassLoader
import java.nio.file.Path
import java.util.jar.JarEntry
import java.util.jar.JarInputStream
import java.util.logging.Level.INFO
import java.util.logging.Level.SEVERE
import java.util.logging.Logger
import kotlin.io.path.listDirectoryEntries

open class ExternalJarLoad {
    val logger = Logger.getLogger(this.javaClass.name)

    open fun loadDependency(
        paths: List<Path>,
        setCurrentClassLoader: Boolean = false,
        classLoader: ClassLoader = this.javaClass.classLoader,
    ): ClassLoader {
        if (paths.isEmpty()) {
            return classLoader
        }
        val jarList =
            paths.flatMap {
                try {
                    it.listDirectoryEntries()
                } catch (t: Throwable) {
                    listOf()
                }
            }
        if (jarList.isEmpty()) {
            return classLoader
        }
        val child = URLClassLoader(jarList.map { it.toUri().toURL() }.toTypedArray(), classLoader)
        val classes =
            jarList
                .flatMap {
                    val classNames =
                        getClassNamesFromJar(it.toString())
                            .map { className ->
                                logger.log(INFO, "Loaded $className")
                                Class.forName(className, true, child)
                            }
                    return@flatMap classNames
                }
        if (setCurrentClassLoader) {
            Thread.currentThread().setContextClassLoader(child)
        }
        return child
    }

    protected open fun getClassNamesFromJar(jarFile: JarInputStream): ArrayList<String> {
        val classNames = ArrayList<String>()
        try {
            var jar: JarEntry?
            while (true) {
                jar = jarFile.nextJarEntry
                if (jar == null) {
                    break
                }
                if (jar.name.endsWith(".class")) {
                    val className: String = jar.name.replace("/", ".")
                    val myClass = className.substring(0, className.lastIndexOf('.'))
                    classNames.add(myClass)
                }
            }
        } catch (e: Throwable) {
            logger.log(SEVERE, "Error load external jar ", e)
        }
        return classNames
    }

    protected open fun getClassNamesFromJar(jarPath: String): ArrayList<String> {
        return getClassNamesFromJar(JarInputStream(FileInputStream(jarPath)))
    }
}

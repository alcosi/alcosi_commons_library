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


/**
 * The `ExternalJarLoad` class provides functionality to dynamically load external JAR files and their classes
 * into the current application.
 *
 * @property logger The logger used for logging messages.
 */
open class ExternalJarLoad {
    val logger = Logger.getLogger(this.javaClass.name)

    /**
     * Loads a dependency from the given list of paths and returns the class loader.
     *
     * @param paths The list of paths specifying the location of the dependency.
     * @param setCurrentClassLoader Flag indicating whether to set the current thread's context class loader to the
     * class loader of the loaded dependency. Default value is false.
     * @param classLoader The parent class loader to use for loading the dependency. Default value is the class loader
     * of the calling class.
     * @return The class loader for the loaded dependency.
     */
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

    /**
     * Retrieves the names of all classes within a JAR file.
     *
     * @param jarFile A JarInputStream representing the JAR file.
     * @return An ArrayList of class names found within the JAR file.
     */
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

    /**
     * Retrieves the names of all classes within a JAR file.
     *
     * @param jarPath The path of the JAR file.
     * @return An ArrayList of class names found within the JAR file.
     */
    protected open fun getClassNamesFromJar(jarPath: String): ArrayList<String> {
        return getClassNamesFromJar(JarInputStream(FileInputStream(jarPath)))
    }
}

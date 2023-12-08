/*
 * Copyright (c) 2023  Alcosi Group Ltd. and affiliates.
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

package com.alcosi.lib.utils

import java.io.FileInputStream
import java.net.URL
import java.net.URLClassLoader
import java.nio.file.Path
import java.util.jar.JarEntry
import java.util.jar.JarInputStream
import java.util.logging.Level.INFO
import java.util.logging.Level.SEVERE
import java.util.logging.Logger
import kotlin.io.path.listDirectoryEntries

open class ExternalJarLoad {
    val logger= Logger.getLogger(this.javaClass.name)

    open fun loadDependency(paths: List<Path>,setCurrentClassLoader:Boolean=false,classLoader :ClassLoader = this.javaClass.classLoader): ClassLoader {
        if (paths.isEmpty()) {
            return classLoader
        }
        val jarList = paths.flatMap {
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
        val classes = jarList
            .flatMap {
                val classNames = getClassNamesFromJar(it.toString())
                    .map { className ->
                        logger.log(INFO,"Loaded $className")
                        Class.forName(className, true, child)
                    }
                return@flatMap classNames
            }
        if (setCurrentClassLoader) {
            Thread.currentThread().setContextClassLoader(child);
        }
        return child
    }


    protected fun getClassNamesFromJar(jarFile: JarInputStream): ArrayList<String> {
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
            logger.log(SEVERE,"Error load external jar ", e)
        }
        return classNames
    }


    protected fun getClassNamesFromJar(jarPath: String): ArrayList<String> {
        return getClassNamesFromJar(JarInputStream(FileInputStream(jarPath)))
    }


}
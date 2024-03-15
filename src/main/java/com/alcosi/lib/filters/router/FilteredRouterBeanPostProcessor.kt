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

package com.alcosi.lib.filters.router

import org.springframework.beans.factory.config.BeanPostProcessor
import org.springframework.web.servlet.function.RouterFunction
import org.springframework.web.servlet.function.ServerResponse
import java.util.logging.Level
import java.util.logging.Logger

open class FilteredRouterBeanPostProcessor(val filters: List<RouterFilter>) : BeanPostProcessor {
    override fun postProcessBeforeInitialization(
        bean: Any,
        beanName: String,
    ): Any? {
        if (bean is RouterFunction<*>) {
            try {
                val mapped = bean as RouterFunction<ServerResponse>
                val withFilters = filters.fold(mapped) { b, f -> b.filter(f) }
                return super.postProcessBeforeInitialization(withFilters, beanName)
            } catch (t: Throwable) {
                logger.log(Level.SEVERE, "Error cast RouterFunction", t)
                return super.postProcessBeforeInitialization(bean, beanName)
            }
        } else {
            return super.postProcessBeforeInitialization(bean, beanName)
        }
    }

    companion object {
        val logger = Logger.getLogger(this.javaClass.name)
    }
}

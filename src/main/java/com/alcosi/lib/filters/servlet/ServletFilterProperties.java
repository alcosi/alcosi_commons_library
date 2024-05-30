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

package com.alcosi.lib.filters.servlet;

import org.springframework.boot.context.properties.ConfigurationProperties;

/**
 * Configuration properties for ServletFilter.
 */
@ConfigurationProperties("common-lib.filter.all")
public class ServletFilterProperties {
    /**
     * Indicates whether the disabled flag is set for the class.
     *
     * <p>
     * The disabled flag is a boolean value that determines if the class is disabled or enabled.
     * By default, the disabled flag is set to false, indicating that the class is enabled.
     * </p>
     *
     * @return the disabled flag value
     *
     * @see ServletFilterProperties
     * @see ServletFilterProperties#getDisabled()
     * @see ServletFilterProperties#setDisabled(Boolean)
     */
    private Boolean disabled = false;
    /**
     * The base order of the ServletFilter.
     *
     * <p>
     * The base order controls the priority at which the ServletFilter is executed.
     * A lower value indicates a higher priority. Integer.MIN_VALUE is used as the default
     * base order value.
     * </p>
     *
     * @see ServletFilterProperties
     * @see ServletFilterProperties#getBaseOrder()
     * @see ServletFilterProperties#setBaseOrder(Integer)
     */
    private Integer baseOrder = Integer.MIN_VALUE;

    public Boolean getDisabled() {
        return disabled;
    }

    public void setDisabled(Boolean disabled) {
        this.disabled = disabled;
    }

    public Integer getBaseOrder() {
        return baseOrder;
    }

    public void setBaseOrder(Integer baseOrder) {
        this.baseOrder = baseOrder;
    }
}

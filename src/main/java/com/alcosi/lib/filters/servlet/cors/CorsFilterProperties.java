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

package com.alcosi.lib.filters.servlet.cors;

import org.springframework.boot.context.properties.ConfigurationProperties;

/**
 * Configuration properties for CorsFilter.
 *
 * This class represents the configuration properties for CorsFilter, which is used to configure the CORS (Cross-Origin Resource Sharing) filter in Spring applications.
 * The properties defined in this class can be used to customize the behavior of the CorsFilter.
 */
@ConfigurationProperties("common-lib.filter.cors")
public class CorsFilterProperties {
    /**
     * Indicates whether the CorsFilter is disabled.
     *
     * This variable represents the disabled flag for the CorsFilter. If the disabled flag is set to true, the CorsFilter will be disabled and will not be applied to the requests
     * . By default, the disabled flag is set to false, indicating that the CorsFilter is enabled.
     *
     * This variable is defined in the CorsFilterProperties class, which is a configuration properties class for CorsFilter.
     */
    private Boolean disabled = false;
    /**
     * The orderDelta variable represents the delta value used for ordering CorsFilter in the filter chain.
     * It is an Integer type and has a default value of 2.
     *
     * The orderDelta value is used to determine the relative ordering of CorsFilter with respect to other filters in the filter chain.
     * A lower orderDelta value places the CorsFilter before other filters with higher values.
     *
     * This variable is defined in the CorsFilterProperties class, which is a configuration properties class for CorsFilter.
     *
     * Example usage:
     * CorsFilterProperties corsFilterProperties = new CorsFilterProperties();
     * Integer order = corsFilterProperties.getOrderDelta();
     */
    private Integer orderDelta = 3;

    public Integer getOrderDelta() {
        return orderDelta;
    }

    public void setOrderDelta(Integer orderDelta) {
        this.orderDelta = orderDelta;
    }


    public Boolean getDisabled() {
        return disabled;
    }

    public void setDisabled(Boolean disabled) {
        this.disabled = disabled;
    }

}

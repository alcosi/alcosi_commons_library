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

package com.alcosi.lib.filters.servlet.principalAuth;

import org.springframework.boot.context.properties.ConfigurationProperties;

/**
 * Configuration properties for PrincipalAuthFilter.
 */
@ConfigurationProperties("common-lib.filter.principal-auth")
public class PrincipalAuthFilterProperties {
    /**
     * The `disabled` variable is a private Boolean variable that represents the current state of the disabled*/
    private Boolean disabled = false;
    /**
     * The orderDelta variable is a private Integer variable that represents the order delta value for PrincipalAuthFilter.
     * It is used to specify the difference in order between PrincipalAuthFilter and other filters.
     * The default value is 0.
     */
    private Integer orderDelta = 0;

    public Boolean getDisabled() {
        return disabled;
    }

    public void setDisabled(Boolean disabled) {
        this.disabled = disabled;
    }

    public Integer getOrderDelta() {
        return orderDelta;
    }

    public void setOrderDelta(Integer orderDelta) {
        this.orderDelta = orderDelta;
    }
}

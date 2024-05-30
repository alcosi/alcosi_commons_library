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

package com.alcosi.lib.objectMapper;

import org.springframework.boot.context.properties.ConfigurationProperties;
/**
 * This class represents the configuration properties for the MappingHelper class.
 * It is annotated with @ConfigurationProperties to specify the prefix of the properties to bind.
 * The prefix used is "common-lib.mapping-helper".
 */
@ConfigurationProperties("common-lib.mapping-helper")
public class MappingHelperProperties {
    /**
     * The `disabled` variable represents the current state of the disable feature.
     *
     * - It is a private variable of type `Boolean`.
     * - The default value is `false`.
     * - It is used within the `MappingHelperProperties` class.
     * - It can be accessed using the `getDisabled()` method to retrieve its value.
     * - To modify its value, use the `setDisabled(Boolean disabled)` method.
     */
    private Boolean disabled = false;

    public Boolean getDisabled() {
        return disabled;
    }

    public void setDisabled(Boolean disabled) {
        this.disabled = disabled;
    }
}

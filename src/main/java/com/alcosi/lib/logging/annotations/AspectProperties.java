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

package com.alcosi.lib.logging.annotations;

import org.springframework.boot.context.properties.ConfigurationProperties;

/**
 * This class represents the configuration properties for the AspectProperties aspect.
 * It is annotated with @ConfigurationProperties to indicate that the properties*/
@ConfigurationProperties("common-lib.aspect")
public class AspectProperties {
    /**
     * This variable represents the state of whether the aspect is disabled or not.
     * By default, it is set to false.
     *
     * @see AspectProperties#getDisabled()
     * @see AspectProperties#setDisabled(Boolean)
     */
    private Boolean disabled = false;


    public Boolean getDisabled() {
        return disabled;
    }

    public void setDisabled(Boolean disabled) {
        this.disabled = disabled;
    }

}

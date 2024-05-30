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

package com.alcosi.lib.filters.router;

import org.springframework.boot.context.properties.ConfigurationProperties;

/**
 * Configuration properties for the RouterFilterCaughtExceptionsProperties class.
 */
@ConfigurationProperties("common-lib.router-filter.caught-exception")
public class RouterFilterCaughtExceptionsProperties {
    /**
     * Indicates whether the feature is disabled or not.
     * The default value is false.
     */
    private Boolean disabled = false;
    /**
     * The errorCode used for message conversion errors.
     * The default value is 400000.
     */
    private Integer messageConversionErrorCode = 400000;
    /**
     * Represents the unknown error code used in the RouterFilterCaughtExceptionsProperties class.
     * The default value is 500000.
     */
    private Integer unknownErrorCode = 500000;
    public Boolean getDisabled() {
        return disabled;
    }

    public void setDisabled(Boolean disabled) {
        this.disabled = disabled;
    }

    public Integer getMessageConversionErrorCode() {
        return messageConversionErrorCode;
    }

    public void setMessageConversionErrorCode(Integer messageConversionErrorCode) {
        this.messageConversionErrorCode = messageConversionErrorCode;
    }

    public Integer getUnknownErrorCode() {
        return unknownErrorCode;
    }

    public void setUnknownErrorCode(Integer unknownErrorCode) {
        this.unknownErrorCode = unknownErrorCode;
    }

}

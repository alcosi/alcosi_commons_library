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

package com.alcosi.lib.filters.servlet.auth;

import org.springframework.boot.context.properties.ConfigurationProperties;

/**
 * The AuthFilterProperties class represents the properties used for configuring the AuthFilter.
 * It is annotated with @ConfigurationProperties to specify the prefix for the properties.
 */
@ConfigurationProperties("common-lib.filter.auth")
public class AuthFilterProperties {
    /**
     * The disabled variable represents whether the AuthFilter is disabled or not.
     * By default, it is set to false, indicating that the AuthFilter is enabled.
     * If set to true, the AuthFilter will be disabled.
     */
    private Boolean disabled=false;
    /**
     * The accessKey variable represents the access key used for authentication.
     * It is a private string variable that holds the value of the access key.
     *
     * This variable is a member of the AuthFilterProperties class.
     *
     * @see AuthFilterProperties
     */
    private String accessKey;
    /**
     * The orderDelta variable represents the difference between the order of the AuthFilter and the order of other filters.
     * The default value of orderDelta is 4.
     */
    private Integer orderDelta = 4;
    /**
     * The wrongEnvErrorCode variable represents the error code returned when the environment is incorrect.
     * It is an Integer type variable.
     * The default value is 401002.
     */
    private Integer wrongEnvErrorCode = 401002;
    /**
     * The wrongAccessKeyErrorCode variable represents the error code returned when the access key is incorrect.
     * It is an Integer type variable.
     * The default value is 401000.
     */
    private Integer wrongAccessKeyErrorCode = 401000;
    /**
     * The noAccessKeyErrorCode variable represents the error code returned when the access key is not provided.
     * It is an Integer type variable.
     * The default value is 401001.
     */
    private Integer noAccessKeyErrorCode = 401001;

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

    public String getAccessKey() {
        return accessKey;
    }

    public void setAccessKey(String accessKey) {
        this.accessKey = accessKey;
    }

    public Integer getWrongEnvErrorCode() {
        return wrongEnvErrorCode;
    }

    public void setWrongEnvErrorCode(Integer wrongEnvErrorCode) {
        this.wrongEnvErrorCode = wrongEnvErrorCode;
    }

    public Integer getWrongAccessKeyErrorCode() {
        return wrongAccessKeyErrorCode;
    }

    public void setWrongAccessKeyErrorCode(Integer wrongAccessKeyErrorCode) {
        this.wrongAccessKeyErrorCode = wrongAccessKeyErrorCode;
    }

    public Integer getNoAccessKeyErrorCode() {
        return noAccessKeyErrorCode;
    }

    public void setNoAccessKeyErrorCode(Integer noAccessKeyErrorCode) {
        this.noAccessKeyErrorCode = noAccessKeyErrorCode;
    }
}

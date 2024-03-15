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

package com.alcosi.lib.synchronisation;

import org.springframework.boot.context.properties.ConfigurationProperties;

import java.time.Duration;

@ConfigurationProperties("common-lib.synchronisation")
public class SynchronisationProperties {
    private Boolean disabled = false;
    private Duration lockTimeout=Duration.ofMinutes(10);
    private Duration clearDelay=Duration.ofMinutes(1);

    public Duration getClearDelay() {
        return clearDelay;
    }

    public void setClearDelay(Duration clearDelay) {
        this.clearDelay = clearDelay;
    }

    public Duration getLockTimeout() {
        return lockTimeout;
    }

    public void setLockTimeout(Duration lockTimeout) {
        this.lockTimeout = lockTimeout;
    }

    public Boolean getDisabled() {
        return disabled;
    }

    public void setDisabled(Boolean disabled) {
        this.disabled = disabled;
    }

}

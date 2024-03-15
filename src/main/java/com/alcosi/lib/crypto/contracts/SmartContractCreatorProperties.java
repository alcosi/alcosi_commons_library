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

package com.alcosi.lib.crypto.contracts;

import org.springframework.boot.context.properties.ConfigurationProperties;

import java.time.Duration;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.logging.Logger;

@ConfigurationProperties("common-lib.crypto.smart-contract-creator")

public class SmartContractCreatorProperties {
    private  Duration lifetime= Duration.ofMinutes(10);
    private String pk=generateRandomPk();
    private Duration clearDelay=Duration.ofSeconds(2);
    private Boolean disabled= false;

    public Duration getClearDelay() {
        return clearDelay;
    }

    public void setClearDelay(Duration clearDelay) {
        this.clearDelay = clearDelay;
    }

    public Boolean getDisabled() {
        return disabled;
    }

    public void setDisabled(Boolean disabled) {
        this.disabled = disabled;
    }

    public Duration getLifetime() {
        return lifetime;
    }

    public void setLifetime(Duration lifetime) {
        this.lifetime = lifetime;
    }

    public String getPk() {
        return pk;
    }

    public void setPk(String pk) {
        this.pk = pk;
    }

    static final Logger logger=Logger.getLogger(SmartContractCreatorProperties.class.getName());

    static public String generateRandomPk() {
        List<Character> alphabet = new ArrayList<>();
        for (char c = 'a'; c <= 'f'; c++) {
            alphabet.add(c);
        }
        for (char c = '0'; c <= '9'; c++) {
            alphabet.add(c);
        }
        StringBuilder pk = new StringBuilder();
        for (int i = 0; i < 64; i++) {
            pk.append(alphabet.get(new Random().nextInt(alphabet.size())));
        }
        logger.info("Node pk generated:" + pk);
        return pk.toString();
    }
}

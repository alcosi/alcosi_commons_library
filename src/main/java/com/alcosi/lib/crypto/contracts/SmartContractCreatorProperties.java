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
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Configuration properties for SmartContractCreator.
 */
@ConfigurationProperties("common-lib.crypto.smart-contract-creator")
public class SmartContractCreatorProperties {
    /**
     * Represents the duration of the variable lifetime.
     *
     * <p>The lifetime duration determines the duration for which the variable is valid.</p>
     *
     * @see Duration
     */
    private  Duration lifetime= Duration.ofMinutes(10);
    /**
     * Represents the private key.
     *
     * <p>This variable stores the private key used for cryptographic operations. The private key is generated using the {@link #generateRandomPk()} method. It is a randomly generated
     *  string of 64 characters, consisting of alphanumeric characters (0-9, a-f). The private key is used for secure communication and data encryption.</p>
     */
    private String pk=generateRandomPk();
    /**
     * Represents the delay time for clearing a variable.
     *
     * <p>This variable determines the time duration after which the variable will be cleared. The clear delay time is measured in seconds.</p>
     *
     * @see Duration
     */
    private Duration clearDelay=Duration.ofSeconds(2);
    /**
     * Represents the status of the 'disabled' flag.
     *
     * <p>
     *     The 'disabled' flag indicates whether the functionality associated with this variable is disabled or enabled.
     *     If the flag is set to 'true', the functionality is disabled. If the flag is set to 'false', the functionality is enabled.
     * </p>
     */
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

    /**
     * The logger variable is an instance of the Logger class from the Java logging API.
     * It is used for logging messages and events in the SmartContractCreatorProperties class.
     *
     * The Logger.getLogger() method is used to retrieve a Logger instance with the specified name.
     * The name is obtained from the SmartContractCreatorProperties class using the getName() method.
     *
     * The logger is declared as static and final, indicating that it is a constant variable that can be accessed by all instances of the class.
     *
     * The logger instance can be used to log messages at different levels, such as INFO, WARNING, SEVERE, etc.
     * These log messages are typically used for debugging and monitoring purposes.
     *
     * Example usage:
     * logger.info("This is an information message");
     * logger.warning("This is a warning message");
     * logger.severe("This is a severe message");
     */
    static final Logger logger=Logger.getLogger(SmartContractCreatorProperties.class.getName());

    /**
     * Generates a random private key (pk) for cryptographic operations.
     *
     * <p>
     * The private key is a randomly generated string of 64 characters, consisting
     * of alphanumeric characters (0-9, a-f).
     * </p>
     *
     * @return the randomly generated private key (pk)
     */
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
        logger.log(Level.FINEST,"Node pk generated:" + pk);
        return pk.toString();
    }
}

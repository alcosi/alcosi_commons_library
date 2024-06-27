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

package com.alcosi.lib.secured.encrypt;

import org.springframework.boot.context.properties.ConfigurationProperties;

/**
 * Configuration properties for encryption-related settings.
 */
@ConfigurationProperties("common-lib.secured")
public class EncryptionProperties {
    public String getPrivate() {
        return getPrivateKey();
    }

    public void setPrivate(String privateKey) {
        setPrivateKey(privateKey);
    }

    public String getPublic() {
        return getPublicKey();
    }

    public void setPublic(String publicKey) {
        setPublicKey(publicKey);
    }

    public String getPrivateKey() {
        return privateKey;
    }

    public void setPrivateKey(String privateKey) {
        this.privateKey = privateKey;
    }

    public String getPublicKey() {
        if (getMode()==MODE.AES && publicKey == null) {
            return privateKey;
        } else {
            return publicKey;
        }
    }

    public void setPublicKey(String publicKey) {
        this.publicKey = publicKey;
    }

    public String getUri() {
        return uri;
    }

    public void setUri(String uri) {
        this.uri = uri;
    }

    public String getAccessKey() {
        return accessKey;
    }

    public void setAccessKey(String accessKey) {
        this.accessKey = accessKey;
    }

    public MODE getMode() {
        return mode;
    }

    public void setMode(MODE mode) {
        this.mode = mode;
    }

    public Boolean getDisabled() {
        return disabled;
    }

    public void setDisabled(Boolean disabled) {
        this.disabled = disabled;
    }
    /**
     * The private key used for encryption.
     */
    private  String privateKey;
    /**
     * Represents the public key used for encryption in the EncryptionProperties class.
     */
    private  String publicKey;
    /**
     * The uniform resource identifier (URI) for accessing a resource.
     */
    private String uri;
    /**
     * The accessKey variable represents the access key used for encryption-related settings.
     * <p>
     * It is a private variable of type String.
     * <p>
     * This variable is declared in the EncryptionProperties class, which is a configuration class
     * for encryption-related settings. It is used to set and get the access key value.
     * <p>
     * The access key can be obtained and modified through the following methods:
     * - setAccessKey(String accessKey): Sets the access key.
     * - getAccessKey(): Returns the current access key.
     * <p>
     * String accessKey = properties.getAccessKey();
     */
    private String accessKey;
    /**
     * The mode variable represents the encryption mode used in the application.
     * It is an instance of the ENUM class MODE, which has two possible values: AES and RSA.
     * The default value of mode is AES.
     * <p>
     * The mode is used in the EncryptionProperties class to determine the type of encryption to be used.
     *
     * @see EncryptionProperties
     */
    private MODE mode = MODE.AES;
    /**
     * Determines if the encryption functionality is disabled.
     * <p>
     * By default, encryption is enabled (disabled = false).
     *
     */
    private Boolean disabled = false;
    /**
     *
     */
    public  enum MODE {
        AES, RSA,MASKING
    }
}

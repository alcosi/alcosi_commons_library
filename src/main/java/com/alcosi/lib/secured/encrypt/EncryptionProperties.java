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

    private  String privateKey;
    private  String publicKey;
    private String uri;
    private String accessKey;
    private MODE mode = MODE.AES;
    private Boolean disabled = false;

    public  enum MODE {
        AES, RSA
    }
}

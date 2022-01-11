package com.api.model;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.NoArgsConstructor;

import javax.xml.bind.annotation.XmlRootElement;
import java.io.Serializable;

@Builder
@XmlRootElement
@AllArgsConstructor
@NoArgsConstructor
@JsonIgnoreProperties(ignoreUnknown = true)
public class JWK implements Serializable {

        @JsonProperty("kid")
        private String keyId;

        @JsonProperty("n")
        private String modulus;

        @JsonProperty("e")
        private String exponent;

        @JsonProperty("kty")
        private String keyType;

        @JsonProperty("alg")
        private String algorithm;

        public String getKeyId() {
            return keyId;
        }

        public void setKeyId(String keyId) {
            this.keyId = keyId;
        }

        public String getModulus() {
            return modulus;
        }

        public void setModulus(String modulus) {
            this.modulus = modulus;
        }

        public String getExponent() {
            return exponent;
        }

        public void setExponent(String exponent) {
            this.exponent = exponent;
        }

        public String getKeyType() {
            return keyType;
        }

        public void setKeyType(String keyType) {
            this.keyType = keyType;
        }

        public String getAlgorithm() {
            return algorithm;
        }

        public void setAlgorithm(String algorithm) {
            this.algorithm = algorithm;
        }
    }


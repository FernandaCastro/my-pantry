package com.fcastro.utils;

import org.apache.commons.codec.binary.Hex;
import org.junit.jupiter.api.extension.BeforeAllCallback;
import org.junit.jupiter.api.extension.ExtensionContext;

import java.security.SecureRandom;
import java.util.concurrent.atomic.AtomicBoolean;

public class SecretKeyInitializer implements BeforeAllCallback {

    private static final AtomicBoolean INITIAL_INVOCATION = new AtomicBoolean(Boolean.TRUE);

    @Override
    public void beforeAll(ExtensionContext context) {
        if (INITIAL_INVOCATION.getAndSet(Boolean.FALSE)) {
            SecureRandom secureRandom = new SecureRandom();
            byte[] secretKey = new byte[64];
            secureRandom.nextBytes(secretKey);
            System.setProperty("security-config.secret", Hex.encodeHexString(secretKey));


//            var secretKey = Keys.secretKeyFor(SignatureAlgorithm.HS512).getEncoded();
//            var base64EncodedSecretKey = Encoders.BASE64.encode(secretKey);
//            System.setProperty("security-config.secret", base64EncodedSecretKey);
        }
    }
}

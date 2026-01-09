package com.fcastro.accountservice.authentication;

import com.fcastro.accountservice.exception.KeyPairException;
import com.fcastro.commons.config.MessageTranslator;
import org.springframework.stereotype.Component;

import javax.crypto.Cipher;
import java.security.KeyFactory;
import java.security.KeyPairGenerator;
import java.security.PrivateKey;
import java.security.PublicKey;
import java.security.spec.PKCS8EncodedKeySpec;
import java.util.Base64;

@Component
public class RSAUtil {

    private static final String ALGORITHM = "RSA";
    private PublicKey publicKey;
    private PrivateKey privateKey;

    public RSAUtil() throws Exception {
        KeyPairGenerator keyGen = KeyPairGenerator.getInstance(ALGORITHM);
        ;
        keyGen.initialize(1024);
        var keyPair = keyGen.generateKeyPair();
        this.publicKey = keyPair.getPublic();
        this.privateKey = keyPair.getPrivate();
    }

    public KeyPairDto getPublicKey() {
        var encodedPublicKey = Base64.getEncoder().encodeToString(publicKey.getEncoded());
        return KeyPairDto.builder().data(encodedPublicKey).build();
    }

    public String encrypt(String rawData) {
        try {
            Cipher cipher = Cipher.getInstance(ALGORITHM);
            cipher.init(Cipher.ENCRYPT_MODE, privateKey);

            byte[] encryptedBytes = cipher.doFinal(rawData.getBytes());
            return Base64.getEncoder().encodeToString(encryptedBytes);
        } catch (Exception ex) {
            throw new KeyPairException(MessageTranslator.getMessage("error.encrypt.data"), ex);
        }
    }

    /**
     * RSA Decryption
     **/
    public String decrypt(String encryptedData) {
        try {
            var keyBytes = privateKey.getEncoded();
            PKCS8EncodedKeySpec spec = new PKCS8EncodedKeySpec(keyBytes);
            KeyFactory keyFactory = KeyFactory.getInstance(ALGORITHM);
            PrivateKey privateKey = keyFactory.generatePrivate(spec);

            Cipher cipher = Cipher.getInstance(ALGORITHM);
            cipher.init(Cipher.DECRYPT_MODE, privateKey);

            byte[] decodedBytes = Base64.getDecoder().decode(encryptedData);
            byte[] decryptedBytes = cipher.doFinal(decodedBytes);

            return new String(decryptedBytes);
        } catch (Exception ex) {
            throw new KeyPairException(MessageTranslator.getMessage("error.decrypt.data"), ex);
        }
    }
}

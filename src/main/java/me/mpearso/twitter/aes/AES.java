package me.mpearso.twitter.aes;

import javax.crypto.KeyGenerator;
import javax.crypto.SecretKey;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;

public class AES {

    /**
     * Generates and returns a random 16 byte initial variable
     * @return a 16 byte array
     */
    public static byte[] getRandomInitialVariable() {
        byte[] iv = new byte[16];
        new SecureRandom().nextBytes(iv);
        return iv;
    }

    /**
     * Generates a 256 bit AES key
     * @return a secret key
     * @throws NoSuchAlgorithmException
     */
    public static SecretKey getAESKey() throws NoSuchAlgorithmException {
        KeyGenerator generator = KeyGenerator.getInstance("AES");
        generator.init(256, SecureRandom.getInstanceStrong());
        return generator.generateKey();
    }

}

package com.shopbee.orderservice.security;

import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;
import java.nio.charset.StandardCharsets;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.util.Base64;

public class SecureKeyUtil {

    private static final String SECRET = "secret";
    private static final String MESSAGE = "message";

    public static String generateHMAC() {
        try {
            Mac hmac = Mac.getInstance("HmacSHA256");
            SecretKeySpec secretKeySpec = new SecretKeySpec(SECRET.getBytes(StandardCharsets.UTF_8), "HmacSHA256");
            hmac.init(secretKeySpec);

            byte[] hash = hmac.doFinal(MESSAGE.getBytes(StandardCharsets.UTF_8));
            return Base64.getEncoder().encodeToString(hash);
        } catch (NoSuchAlgorithmException | InvalidKeyException e) {
            return "";
        }
    }

    public static boolean verifyHMAC(String receivedHmac) {
        String calculatedHmac = SecureKeyUtil.generateHMAC();
        return calculatedHmac.equals(receivedHmac);
    }

}

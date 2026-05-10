package com.nhantic.trelloapi.util;

import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;
import java.nio.charset.StandardCharsets;
import java.util.Base64;

public class CognitoSecretHash {
    public static String generateSecretHash(
            String username,
            String clientId,
            String clientSecret
    ) {
        try {
            String data = username + clientId;

            Mac mac = Mac.getInstance("HmacSHA256");

            mac.init(new SecretKeySpec(
                    clientSecret.getBytes(StandardCharsets.UTF_8),
                    "HmacSHA256"
            ));

            byte[] raw = mac.doFinal(data.getBytes(StandardCharsets.UTF_8));

            return Base64.getEncoder().encodeToString(raw);

        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
}

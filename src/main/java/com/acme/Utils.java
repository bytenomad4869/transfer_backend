package com.acme;

import java.util.UUID;

public class Utils {
    public static String generateUUID(boolean noDashes) {
        String uuid = UUID.randomUUID().toString();

        if (noDashes) return uuid.replace("-", "");

        return uuid;
    }
}

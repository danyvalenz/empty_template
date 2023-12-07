package com.coppel.util;

import lombok.experimental.UtilityClass;

@UtilityClass
public class Vulnerabilidades {

    public String sanitiziedString(String str){
        int[] asciiCodes = new int[str.length()];

        // Copy character by character into array
        for (int i = 0; i < str.length(); i++) {
            asciiCodes[i] = str.charAt(i);
        }

        StringBuilder sanitiziedString = new StringBuilder();

        for (int asciiCode : asciiCodes) {
            sanitiziedString.append((char) asciiCode);
        }
        return sanitiziedString.toString();
    }

    public static boolean checkAuthorization(String username) {
        return username.equals("authorization");
    }
}

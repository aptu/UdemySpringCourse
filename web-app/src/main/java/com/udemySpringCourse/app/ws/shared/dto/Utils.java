package com.udemySpringCourse.app.ws.shared.dto;

import org.springframework.stereotype.Component;

import java.security.SecureRandom;
import java.util.Random;

@Component
public class Utils {
    private final Random RANDOM = new SecureRandom();
    private final String ALPHABET = "0123456789ABCDEFGHIJKLMNopqrstuvwxyz";
    private final int ITERATION = 10000;
    private final int KEY_LENGTH = 256;

    public String generateUserId(int length) {
        return generatedRandomString(length);
    }

    public String generateAddressId(int length) {
        return generatedRandomString(length);
    }

    private String generatedRandomString(int length){
        StringBuilder returnValue = new StringBuilder(length);

        for (int i=0; i<length; i++) {
            returnValue.append(ALPHABET.charAt(RANDOM.nextInt(ALPHABET.length())));
        }
        return new String(returnValue);
    }
}

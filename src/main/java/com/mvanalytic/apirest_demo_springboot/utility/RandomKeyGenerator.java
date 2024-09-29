package com.mvanalytic.apirest_demo_springboot.utility;

import java.security.SecureRandom;

public class RandomKeyGenerator {
  // Definir el conjunto de caracteres permitidos (solo letras mayúsculas y minúsculas)
    private static final String LETTERS = "ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz";
    private static final SecureRandom RANDOM = new SecureRandom();

    public static String generateRandomKey(int length) {
        StringBuilder key = new StringBuilder(length);
        for (int i = 0; i < length; i++) {
            int index = RANDOM.nextInt(LETTERS.length());
            key.append(LETTERS.charAt(index));
        }
        return key.toString();
    }
    
}

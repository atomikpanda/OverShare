package com.baileyseymour.overshare.utils;

import java.util.Random;

public class IdGenerator {
    private static Random RANDOM = new Random();

    public static byte[] randomBytes(int length) {
        byte[] bytes = new byte[length];
        RANDOM.nextBytes(bytes);
        return bytes;
    }
}

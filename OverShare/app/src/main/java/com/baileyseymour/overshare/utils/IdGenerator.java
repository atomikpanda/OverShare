// Bailey Seymour
// DVP6 - 1903
// IdGenerator.java

package com.baileyseymour.overshare.utils;

import java.util.Random;

public class IdGenerator {
    private static Random RANDOM = new Random();

    // Generates a random byte array of a specific size
    public static byte[] randomBytes(int length) {
        byte[] bytes = new byte[length];
        RANDOM.nextBytes(bytes);
        return bytes;
    }
}

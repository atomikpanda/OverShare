// Bailey Seymour
// DVP6 - 1903
// OutputType.java

package com.baileyseymour.overshare.enums;

public enum OutputType {
    TEXT("text"),
    URL("url");

    final String stringValue;

    OutputType(String stringValue) {
        this.stringValue = stringValue;
    }

    public static OutputType fromString(String string) {
        for (OutputType b : OutputType.values()) {
            if (b.stringValue.equalsIgnoreCase(string)) {
                return b;
            }
        }
        return URL;
    }
}

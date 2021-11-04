package com.robin.baseframe.app.util;

import java.text.DecimalFormat;

public class NumberUtils {


    public static float range(float value, float min, float max) {
        return Math.max(min, Math.min(value, max));
    }

    public static double range(double value, double min, double max) {
        return Math.max(min, Math.min(value, max));
    }

    public static int range(int value, int min, int max) {
        return Math.max(min, Math.min(value, max));
    }

    public static long range(long value, long min, long max) {
        return Math.max(min, Math.min(value, max));
    }

    public static boolean inRange(double value, double min, double max) {
        return value >= min && value <= max;
    }

    public static String formatDecimal(double value, int... saveCount) {
        StringBuilder pattern;
        if (saveCount != null && saveCount.length > 0) {
            pattern = new StringBuilder("0.");
            for (int i = 0; i < saveCount[0]; i++) {
                pattern.append("0");
            }
        } else {
            pattern = new StringBuilder("0");
        }

        DecimalFormat formatter = new DecimalFormat();
        formatter.applyPattern(pattern.toString());
        return formatter.format(value);
    }

}


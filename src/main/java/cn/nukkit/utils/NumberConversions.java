package cn.nukkit.utils;

import javax.annotation.Nullable;

/**
 * Utils for casting number types to other number types.
 */
public final class NumberConversions {
    public static byte toByte(@Nullable Object object) {
        return toByte(object, (byte) 0);
    }

    public static byte toByte(@Nullable Object object, byte defaultValue) {
        if (object instanceof Number number) {
            return number.byteValue();
        }

        if (object == null) {
            return defaultValue;
        }

        try {
            return Byte.parseByte(object.toString());
        } catch (NumberFormatException e) {
            return defaultValue;
        }
    }

    public static short toShort(@Nullable Object object) {
        return toShort(object, (short) 0);
    }

    public static short toShort(@Nullable Object object, short defaultValue) {
        if (object instanceof Number number) {
            return number.shortValue();
        }

        if (object == null) {
            return defaultValue;
        }

        try {
            return Short.parseShort(object.toString());
        } catch (NumberFormatException e) {
            return defaultValue;
        }
    }

    public static int toInt(@Nullable Object object) {
        return toInt(object, 0);
    }

    public static int toInt(@Nullable Object object, int defaultValue) {
        if (object instanceof Number number) {
            return number.intValue();
        }

        if (object == null) {
            return defaultValue;
        }

        try {
            return Integer.parseInt(object.toString());
        } catch (NumberFormatException e) {
            return defaultValue;
        }
    }

    public static long toLong(@Nullable Object object) {
        return toLong(object, 0);
    }

    public static long toLong(@Nullable Object object, long defaultValue) {
        if (object instanceof Number number) {
            return number.longValue();
        }

        if (object == null) {
            return defaultValue;
        }

        try {
            return Long.parseLong(object.toString());
        } catch (NumberFormatException e) {
            return defaultValue;
        }
    }

    public static float toFloat(@Nullable Object object) {
        return toFloat(object, 0);
    }

    public static float toFloat(@Nullable Object object, float defaultValue) {
        if (object instanceof Number number) {
            return number.floatValue();
        }

        if (object == null) {
            return defaultValue;
        }

        try {
            return Float.parseFloat(object.toString());
        } catch (NumberFormatException e) {
            return defaultValue;
        }
    }

    public static double toDouble(@Nullable Object object) {
        return toDouble(object, 0);
    }

    public static double toDouble(@Nullable Object object, double defaultValue) {
        if (object instanceof Number number) {
            return number.doubleValue();
        }

        if (object == null) {
            return defaultValue;
        }

        try {
            return Double.parseDouble(object.toString());
        } catch (NumberFormatException e) {
            return defaultValue;
        }
    }

    private NumberConversions() {
    }
}

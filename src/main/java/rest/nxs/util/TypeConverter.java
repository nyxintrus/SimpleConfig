package rest.nxs.util;

import java.util.ArrayList;
import java.util.List;

public class TypeConverter {

    public static int toInt(Object obj, int def) {
        if (obj instanceof Number) return ((Number) obj).intValue();
        try {
            return Integer.parseInt(String.valueOf(obj));
        } catch (Exception e) {
            return def;
        }
    }

    public static double toDouble(Object obj, double def) {
        if (obj instanceof Number) return ((Number) obj).doubleValue();
        try {
            return Double.parseDouble(String.valueOf(obj));
        } catch (Exception e) {
            return def;
        }
    }

    public static boolean toBoolean(Object obj, boolean def) {
        if (obj instanceof Boolean) return (Boolean) obj;
        if (obj != null) return Boolean.parseBoolean(obj.toString());
        return def;
    }

    public static String toString(Object obj, String def) {
        return obj != null ? obj.toString() : def;
    }

    public static List<String> toStringList(Object obj) {
        if (obj instanceof List<?> list) {
            List<String> result = new ArrayList<>();
            for (Object o : list) {
                result.add(String.valueOf(o));
            }
            return result;
        }
        return new ArrayList<>();
    }

    public static List<Integer> toIntList(Object obj) {
        if (obj instanceof List<?> list) {
            List<Integer> result = new ArrayList<>();
            for (Object o : list) {
                result.add(toInt(o, 0));
            }
            return result;
        }
        return new ArrayList<>();
    }
}
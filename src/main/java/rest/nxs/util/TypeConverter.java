package rest.nxs.util;

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
}
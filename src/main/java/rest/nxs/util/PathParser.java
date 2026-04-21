package rest.nxs.util;

import java.util.HashMap;
import java.util.Map;

public class PathParser {

    public static Object get(Map<String, Object> map, String path) {
        String[] parts = path.split("\\.");
        Object current = map;

        for (String part : parts) {
            if (!(current instanceof Map)) return null;

            Map<?, ?> currentMap = (Map<?, ?>) current;

            current = currentMap.get(part);
        }

        return current;
    }

    @SuppressWarnings("unchecked")
    public static void set(Map<String, Object> map, String path, Object value) {
        String[] parts = path.split("\\.");
        Map<String, Object> current = map;

        for (int i = 0; i < parts.length - 1; i++) {
            String key = parts[i];

            Object next = current.get(key);

            if (!(next instanceof Map)) {
                next = new HashMap<String, Object>();
                current.put(key, next);
            }

            current = (Map<String, Object>) next;
        }

        current.put(parts[parts.length - 1], value);
    }
}
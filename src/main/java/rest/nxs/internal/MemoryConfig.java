package rest.nxs.internal;

import rest.nxs.util.PathParser;

import java.util.HashMap;
import java.util.Map;

public class MemoryConfig {

    private final Map<String, Object> data = new HashMap<>();

    public Object get(String path) {
        return PathParser.get(data, path);
    }

    public void set(String path, Object value) {
        PathParser.set(data, path, value);
    }

    public boolean contains(String path) {
        return get(path) != null;
    }

    public Map<String, Object> getRaw() {
        return data;
    }
}
package rest.nxs.internal;

import rest.nxs.util.PathParser;

import java.util.*;

public class MemoryConfig {

    private final Map<String, Object> data = new HashMap<>();

    public Object get(String path) {
        return PathParser.get(data, path);
    }

    public Object get(String path, Object def) {
        Object val = get(path);
        return val != null ? val : def;
    }

    public void set(String path, Object value) {
        PathParser.set(data, path, value);
    }

    public boolean contains(String path) {
        return PathParser.exists(data, path);
    }

    public void remove(String path) {
        PathParser.remove(data, path);
    }

    public MemoryConfig getSection(String path) {
        Object obj = get(path);
        if (obj instanceof Map<?, ?> map) {
            MemoryConfig section = new MemoryConfig();
            section.data.putAll((Map<String, Object>) map);
            return section;
        }
        return null;
    }

    @SuppressWarnings("unchecked")
    public List<Object> getList(String path) {
        Object obj = get(path);
        if (obj instanceof List<?>) {
            return (List<Object>) obj;
        }
        return new ArrayList<>();
    }

    public Map<String, Object> getRaw() {
        return data;
    }
}
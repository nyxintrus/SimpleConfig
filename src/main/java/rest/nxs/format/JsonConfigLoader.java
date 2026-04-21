package rest.nxs.format;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;
import rest.nxs.config.ConfigLoader;
import rest.nxs.internal.MemoryConfig;

import java.io.*;
import java.lang.reflect.Type;
import java.util.HashMap;
import java.util.Map;

public class JsonConfigLoader implements ConfigLoader {

    private final Gson gson = new GsonBuilder()
            .setPrettyPrinting()
            .create();

    private final Type type = new TypeToken<Map<String, Object>>(){}.getType();

    @Override
    public MemoryConfig load(File file) {
        MemoryConfig config = new MemoryConfig();

        if (!file.exists()) {
            try {
                file.createNewFile();
            } catch (IOException e) {
                e.printStackTrace();
            }
            return config;
        }

        try (FileReader reader = new FileReader(file)) {
            Map<String, Object> raw = gson.fromJson(reader, type);

            if (raw != null) {
                config.getRaw().putAll(convert(raw));
            }

        } catch (Exception e) {
            e.printStackTrace();
        }

        return config;
    }

    @Override
    public void save(File file, MemoryConfig config) {
        try (FileWriter writer = new FileWriter(file)) {
            gson.toJson(config.getRaw(), writer);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private Map<String, Object> convert(Map<String, Object> input) {
        Map<String, Object> result = new HashMap<>();

        for (Map.Entry<String, Object> entry : input.entrySet()) {
            Object value = entry.getValue();

            if (value instanceof Map) {
                value = convert((Map<String, Object>) value);
            }

            result.put(String.valueOf(entry.getKey()), value);
        }

        return result;
    }
}
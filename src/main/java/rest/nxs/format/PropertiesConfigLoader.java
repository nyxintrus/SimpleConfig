package rest.nxs.format;

import rest.nxs.config.ConfigLoader;
import rest.nxs.internal.MemoryConfig;

import java.io.*;
import java.util.Map;
import java.util.Properties;

public class PropertiesConfigLoader implements ConfigLoader {

    @Override
    public MemoryConfig load(File file) {
        MemoryConfig config = new MemoryConfig();
        Properties props = new Properties();

        try {
            if (!file.exists()) {
                file.createNewFile();
                return config;
            }

            try (FileReader reader = new FileReader(file)) {
                props.load(reader);
            }

            for (String key : props.stringPropertyNames()) {
                config.set(key, props.getProperty(key));
            }

        } catch (Exception e) {
            e.printStackTrace();
        }

        return config;
    }

    @Override
    public void save(File file, MemoryConfig config) {
        Properties props = new Properties();

        flatten("", config.getRaw(), props);

        try (FileWriter writer = new FileWriter(file)) {
            props.store(writer, "Generated Config");
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void flatten(String prefix, Object obj, Properties props) {
        if (obj instanceof Map<?, ?> map) {
            for (var entry : map.entrySet()) {
                String key = prefix.isEmpty() ? entry.getKey().toString()
                        : prefix + "." + entry.getKey();
                flatten(key, entry.getValue(), props);
            }
        } else {
            props.setProperty(prefix, String.valueOf(obj));
        }
    }
}
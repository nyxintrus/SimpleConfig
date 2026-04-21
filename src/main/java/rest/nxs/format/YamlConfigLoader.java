package rest.nxs.format;

import org.yaml.snakeyaml.Yaml;
import rest.nxs.config.ConfigLoader;
import rest.nxs.internal.MemoryConfig;

import java.io.*;
import java.util.HashMap;
import java.util.Map;

public class YamlConfigLoader implements ConfigLoader {

    private final Yaml yaml = new Yaml();

    @Override
    public MemoryConfig load(File file) {
        MemoryConfig config = new MemoryConfig();

        if (!file.exists()) {
            try {
                file.createNewFile(); // Auto create
            } catch (IOException e) {
                e.printStackTrace();
            }
            return config;
        }

        try (FileReader reader = new FileReader(file)) {
            Map<String, Object> map = yaml.load(reader);
            if (map != null) {
                config.getRaw().putAll(new HashMap<>(map));
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        return config;
    }

    @Override
    public void save(File file, MemoryConfig config) {
        try (FileWriter writer = new FileWriter(file)) {
            yaml.dump(config.getRaw(), writer);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
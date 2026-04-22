package rest.nxs.format;

import com.typesafe.config.ConfigFactory;
import rest.nxs.config.ConfigLoader;
import rest.nxs.internal.MemoryConfig;

import java.io.File;
import java.io.FileWriter;
import java.util.Map;

public class HoconConfigLoader implements ConfigLoader {

    @Override
    public MemoryConfig load(File file) {
        MemoryConfig config = new MemoryConfig();

        try {
            if (!file.exists() || file.length() == 0) {
                return config;
            }

            var conf = ConfigFactory.parseFile(file).resolve();

            for (Map.Entry<String, com.typesafe.config.ConfigValue> entry : conf.entrySet()) {
                config.set(entry.getKey(), entry.getValue().unwrapped());
            }

        } catch (Exception e) {
            e.printStackTrace();
        }

        return config;
    }

    @Override
    public void save(File file, MemoryConfig config) {
        try (FileWriter writer = new FileWriter(file)) {
            writeMap(writer, config.getRaw());
        } catch (Exception e) {
            throw new RuntimeException("Failed to save HOCON", e);
        }
    }

    private void writeMap(FileWriter writer, Map<String, Object> map) throws Exception {

        for (Map.Entry<String, Object> entry : map.entrySet()) {

            String key = entry.getKey();
            Object value = entry.getValue();

            if (value instanceof Map) {

                writer.write("\n[" + key + "]\n");
                writeMap(writer, (Map<String, Object>) value);

            } else if (value instanceof Iterable) {

                writer.write(key + " = " + value.toString() + "\n");

            } else if (value instanceof String) {

                writer.write(key + " = \"" + value + "\"\n");

            } else {

                writer.write(key + " = " + value + "\n");
            }
        }
    }
}
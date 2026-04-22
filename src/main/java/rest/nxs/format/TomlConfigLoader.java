package rest.nxs.format;

import com.moandjiezana.toml.Toml;
import rest.nxs.config.ConfigLoader;
import rest.nxs.internal.MemoryConfig;

import java.io.File;
import java.io.FileWriter;
import java.util.List;
import java.util.Map;

public class TomlConfigLoader implements ConfigLoader {

    @Override
    public MemoryConfig load(File file) {
        MemoryConfig config = new MemoryConfig();

        try {
            if (!file.exists() || file.length() == 0) {
                return config;
            }

            Toml toml = new Toml().read(file);
            Map<String, Object> map = toml.toMap();

            if (map != null) {
                config.getRaw().putAll(map);
            }

        } catch (Exception e) {
            throw new RuntimeException("Failed to load TOML", e);
        }

        return config;
    }

    @Override
    public void save(File file, MemoryConfig config) {
        try (FileWriter writer = new FileWriter(file)) {
            writeTable(writer, config.getRaw());
        } catch (Exception e) {
            throw new RuntimeException("Failed to save TOML", e);
        }
    }

    @SuppressWarnings("unchecked")
    private void writeTable(FileWriter writer, Map<String, Object> map) throws Exception {

        for (var entry : map.entrySet()) {

            String key = entry.getKey();
            Object value = entry.getValue();

            // nested table
            if (value instanceof Map<?, ?> sub) {
                writer.write("\n[" + key + "]\n");
                writeTable(writer, (Map<String, Object>) sub);
            }

            // list → TOML array
            else if (value instanceof List<?> list) {
                writer.write(key + " = " + formatList(list) + "\n");
            }

            // string
            else if (value instanceof String s) {
                writer.write(key + " = \"" + s + "\"\n");
            }

            // number / boolean
            else {
                writer.write(key + " = " + value + "\n");
            }
        }
    }

    private String formatList(List<?> list) {
        StringBuilder sb = new StringBuilder();
        sb.append("[");

        for (int i = 0; i < list.size(); i++) {
            Object v = list.get(i);

            if (v instanceof String) {
                sb.append("\"").append(v).append("\"");
            } else {
                sb.append(v);
            }

            if (i < list.size() - 1) {
                sb.append(", ");
            }
        }

        sb.append("]");
        return sb.toString();
    }
}
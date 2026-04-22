package rest.nxs.format;

import com.typesafe.config.ConfigFactory;
import com.typesafe.config.ConfigValue;
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

            for (var entry : conf.entrySet()) {
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

            StringBuilder sb = new StringBuilder();

            writeMap(sb, "", config.getRaw());

            writer.write(sb.toString());

        } catch (Exception e) {
            throw new RuntimeException("Failed to save HOCON config", e);
        }
    }

    @SuppressWarnings("unchecked")
    private void writeMap(StringBuilder sb, String prefix, Object raw) {

        if (raw instanceof Map<?, ?> map) {

            for (var entry : map.entrySet()) {

                String key = String.valueOf(entry.getKey());
                Object value = entry.getValue();

                String fullKey = prefix.isEmpty() ? key : prefix + "." + key;

                writeMap(sb, fullKey, value);
            }

        } else if (raw instanceof Iterable<?> list) {

            sb.append(prefix).append(" = [");

            boolean first = true;
            for (Object item : list) {
                if (!first) sb.append(", ");
                first = false;

                if (item instanceof String) {
                    sb.append("\"").append(item).append("\"");
                } else {
                    sb.append(item);
                }
            }

            sb.append("]\n");

        } else {

            sb.append(prefix)
                    .append(" = ");

            if (raw instanceof String) {
                sb.append("\"").append(raw).append("\"");
            } else {
                sb.append(raw);
            }

            sb.append("\n");
        }
    }
}
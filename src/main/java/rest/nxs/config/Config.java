package rest.nxs.config;

import rest.nxs.format.JsonConfigLoader;
import rest.nxs.format.YamlConfigLoader;
import rest.nxs.internal.MemoryConfig;
import rest.nxs.util.TypeConverter;

import java.io.File;

public class Config {

    private final MemoryConfig memory;
    private final File file;
    private final ConfigLoader loader;

    private Config(File file, ConfigLoader loader, MemoryConfig memory) {
        this.file = file;
        this.loader = loader;
        this.memory = memory;
    }

    public static Config load(String path) {
        File file = new File(path);

        ConfigLoader loader;

        if (path.endsWith(".json")) {
            loader = new JsonConfigLoader();
        } else if (path.endsWith(".yml") || path.endsWith(".yaml")) {
            loader = new YamlConfigLoader();
        } else {
            throw new ConfigException("Unsupported file format: " + path);
        }

        MemoryConfig memory = loader.load(file);

        return new Config(file, loader, memory);
    }

    public int getInt(String path, int def) {
        return TypeConverter.toInt(memory.get(path), def);
    }

    public double getDouble(String path, double def) {
        return TypeConverter.toDouble(memory.get(path), def);
    }

    public boolean getBoolean(String path, boolean def) {
        return TypeConverter.toBoolean(memory.get(path), def);
    }

    public String getString(String path, String def) {
        return TypeConverter.toString(memory.get(path), def);
    }

    public Object get(String path) {
        return memory.get(path);
    }

    public void set(String path, Object value) {
        memory.set(path, value);
    }

    public boolean contains(String path) {
        return memory.contains(path);
    }

    public void save() {
        loader.save(file, memory);
    }
}
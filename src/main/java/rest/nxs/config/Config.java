package rest.nxs.config;

import rest.nxs.format.*;
import rest.nxs.internal.MemoryConfig;
import rest.nxs.util.TypeConverter;

import java.io.File;
import java.util.List;

public class Config {

    private MemoryConfig memory;
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
        String lower = path.toLowerCase();

        if (lower.endsWith(".json")) {
            loader = new JsonConfigLoader();
        } else if (lower.endsWith(".yml") || lower.endsWith(".yaml")) {
            loader = new YamlConfigLoader();
        } else if (lower.endsWith(".properties")) {
            loader = new PropertiesConfigLoader();
        } else if (lower.endsWith(".toml")) {
            loader = new TomlConfigLoader();
        } else if (lower.endsWith(".conf") || lower.endsWith(".hocon")) {
            loader = new HoconConfigLoader();
        } else if (lower.endsWith(".xml")) {
            loader = new XmlConfigLoader();
        } else {
            throw new ConfigException("Unsupported file format: " + path);
        }

        MemoryConfig memory = loader.load(file);

        // DEBUG (optional)
        System.out.println("Loaded config: " + memory.getRaw());

        return new Config(file, loader, memory);
    }

    public Object get(String path) {
        return memory.get(path);
    }

    public Object get(String path, Object def) {
        return memory.get(path, def);
    }

    public void set(String path, Object value) {
        memory.set(path, value);
    }

    public boolean contains(String path) {
        return memory.contains(path);
    }

    public void remove(String path) {
        memory.remove(path);
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

    public List<String> getStringList(String path) {
        return TypeConverter.toStringList(memory.get(path));
    }

    public List<Integer> getIntList(String path) {
        return TypeConverter.toIntList(memory.get(path));
    }

    public MemoryConfig getSection(String path) {
        return memory.getSection(path);
    }

    public Object getOrSet(String path, Object def) {
        if (!contains(path)) {
            set(path, def);
            return def;
        }
        return get(path);
    }

    public void reload() {
        this.memory = loader.load(file);
    }

    public void save() {
        loader.save(file, memory);
    }
}
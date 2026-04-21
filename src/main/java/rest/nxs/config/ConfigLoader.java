package rest.nxs.config;

import rest.nxs.internal.MemoryConfig;

import java.io.File;

public interface ConfigLoader {

    MemoryConfig load(File file);

    void save(File file, MemoryConfig config);
}
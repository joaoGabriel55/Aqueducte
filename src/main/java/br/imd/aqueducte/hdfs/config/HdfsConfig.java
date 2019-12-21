package br.imd.aqueducte.hdfs.config;

import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.fs.FileSystem;

import java.io.IOException;

public class HdfsConfig {

    private static final String URL = "hdfs://nodemaster:9000";
    private static Configuration configInstance;

    public static synchronized FileSystem getInstance() throws IOException {
        if (configInstance == null)
            configInstance = new Configuration();
        configInstance.set("fs.defaultFS", URL);
        FileSystem fileSystem = FileSystem.get(configInstance);
        return fileSystem;
    }


}

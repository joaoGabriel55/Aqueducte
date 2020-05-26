package br.imd.aqueducte.config;

import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

public class PropertiesParams {
    public static boolean AUTH;
    public static boolean TEST_ENV;
    public static String URL_AQUECONNECT;

    static {
        try {
            initialize();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static void initialize() throws IOException {
        ClassLoader classLoader = Thread.currentThread().getContextClassLoader();
        InputStream source = classLoader.getResourceAsStream("properties.cfg");
        Properties conf = new Properties();
        conf.load(source);
        AUTH = Boolean.parseBoolean(conf.getProperty("AUTH"));
        TEST_ENV = Boolean.parseBoolean(conf.getProperty("AUTH"));
        URL_AQUECONNECT = String.valueOf(conf.getProperty("URL_AQUECONNECT"));
    }
}

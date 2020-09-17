package br.imd.aqueducte.config;

import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

public class PropertiesParams {
    public static boolean USE_USER_ID;
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
        USE_USER_ID = Boolean.parseBoolean(conf.getProperty("USE_USER_ID"));
        URL_AQUECONNECT = String.valueOf(conf.getProperty("URL_AQUECONNECT"));
    }
}

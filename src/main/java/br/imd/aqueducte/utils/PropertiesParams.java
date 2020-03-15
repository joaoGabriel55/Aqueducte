package br.imd.aqueducte.utils;

public class PropertiesParams {
    public static boolean AUTH = false;
    public static final boolean TEST_ENV = true;
    public static int STATUS_OK = 200;
    public final static String ROLE_AQUEDUCTE = "aqueducte";
    public final static String APP_TOKEN = "application-token";
    public final static String USER_TOKEN = "user-token";
    // public static String URL_SGEOL = "http://192.168.7.47/sgeol-dm/v2/"; // MPRN
    // public static String URL_SGEOL = "http://10.7.52.26:8080/sgeol-test-sec/v2/"; // Test
    public static String URL_SGEOL = "http://localhost:8080/sgeol-dm/v2/"; // Localhost
    public static String URL_AQUECONNECT = "http://10.7.128.16:7000/aqueconnect/";
}

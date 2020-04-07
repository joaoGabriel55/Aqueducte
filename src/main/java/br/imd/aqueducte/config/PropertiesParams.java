package br.imd.aqueducte.config;

public class PropertiesParams {
    public static boolean AUTH = true;
    public static final boolean TEST_ENV = false;
    public static int STATUS_OK = 200;
    public final static String ROLE_AQUEDUCTE = "aqueducte";
    public final static String APP_TOKEN = "application-token";
    public final static String USER_TOKEN = "user-token";
    // public static String URL_SGEOL = "http://192.168.7.47/sgeol-dm/v2/"; // MPRN
    public static String URL_SGEOL = "http://sgeolayers.imd.ufrn.br/sgeol-test-sec/v2/"; // Test test-sec
    // public static String URL_SGEOL = "http://localhost:8080/sgeol-dm/v2/"; // Localhost
    public static String URL_AQUECONNECT = "http://10.7.128.16:7000/aqueconnect/";
}

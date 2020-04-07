package br.imd.aqueducte.logger;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class LoggerMessage {
    private static final Logger LOG = LoggerFactory.getLogger(LoggerMessage.class);

    public static void logInfo(String text, Object value) {
        LoggerMessage.LOG.info(text, value);
    }

    public static void logError(String text, Object value) {
        LoggerMessage.LOG.error(text, value);
    }

    public static void logWarning(String text, Object value) {
        LoggerMessage.LOG.warn(text, value);
    }

}

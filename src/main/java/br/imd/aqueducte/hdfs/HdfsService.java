package br.imd.aqueducte.hdfs;

import br.imd.aqueducte.hdfs.config.HdfsConfig;
import org.apache.hadoop.fs.Path;

import java.io.IOException;

import static br.imd.aqueducte.logger.LoggerMessage.logInfo;
import static br.imd.aqueducte.logger.LoggerMessage.logWarning;

public class HdfsService {

    public void createDirectory(String pathDirectory) throws IOException {
        String directoryName = pathDirectory;
        Path path = new Path(directoryName);
        HdfsConfig.getInstance().mkdirs(path);
    }

    public static void checkExists(String pathDirectory) throws IOException {
        String directoryName = pathDirectory;
        Path path = new Path(directoryName);
        if (HdfsConfig.getInstance().exists(path)) {
            logInfo("File/Folder Exists : {}", path.getName());
        } else {
            logWarning("File/Folder does not Exists : {}", path.getName());
        }
    }
}

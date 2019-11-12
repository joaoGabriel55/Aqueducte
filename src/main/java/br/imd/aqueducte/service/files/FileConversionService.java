package br.imd.aqueducte.service.files;

import org.apache.commons.fileupload.FileItemIterator;
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.List;

@Component
public interface FileConversionService {

    List<HashMap<String, Object>> convertToJson(FileItemIterator fileItemIterator, int limit, String delimiter);
}

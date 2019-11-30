package br.imd.aqueducte.controllers;

import br.imd.aqueducte.models.response.Response;
import br.imd.aqueducte.service.files.CsvToJsonService;
import org.apache.commons.fileupload.FileItemIterator;
import org.apache.commons.fileupload.FileUploadException;
import org.apache.commons.fileupload.servlet.ServletFileUpload;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import java.io.IOException;
import java.util.HashMap;
import java.util.List;

@RestController
@RequestMapping("/sync/fileHandler")
@CrossOrigin(origins = "*")
public class FileHandlerController {

    @Autowired
    private CsvToJsonService csvToJsonService;

    @PostMapping(value = "/jsonCsvConverter")
    public ResponseEntity<Response<List<HashMap<String, Object>>>> getCSVToJson(HttpServletRequest uploadFile,
                                                                                @RequestParam(required = true) int limit,
                                                                                @RequestParam(required = true) String delimiter) {
        Response<List<HashMap<String, Object>>> response = new Response<>();

        boolean isMultipart = ServletFileUpload.isMultipartContent(uploadFile);
        if (isMultipart) {
            ServletFileUpload upload = new ServletFileUpload();
            FileItemIterator fileItemIterator;
            try {
                fileItemIterator = upload.getItemIterator(uploadFile);
                List<HashMap<String, Object>> listJson = csvToJsonService.convertToJson(fileItemIterator, limit, delimiter);
                response.setData(listJson);
                return ResponseEntity.ok().body(response);
            } catch (FileUploadException e) {
                response.getErrors().add(e.getMessage());
                return ResponseEntity.badRequest().body(response);
            } catch (IOException e) {
                response.getErrors().add(e.getMessage());
                return ResponseEntity.badRequest().body(response);
            }
        } else {
            response.getErrors().add("File type not accept");
            return ResponseEntity.badRequest().body(response);
        }
    }
}

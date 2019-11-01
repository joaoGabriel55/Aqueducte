package br.imd.aqueducte.controllers;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.util.HashMap;
import java.util.List;

import javax.servlet.http.HttpServletRequest;

import br.imd.aqueducte.utils.CsvToJsonUtil;
import org.apache.commons.fileupload.FileItemIterator;
import org.apache.commons.fileupload.FileItemStream;
import org.apache.commons.fileupload.FileUploadException;
import org.apache.commons.fileupload.servlet.ServletFileUpload;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import br.imd.aqueducte.models.response.Response;

@RestController
@RequestMapping("/sync/fileHandler")
@CrossOrigin(origins = "*")
public class FileHandlerController {

    @PostMapping(value = "/jsonConverter")
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
                List<HashMap<String, Object>> listJson = CsvToJsonUtil.convertCsvToJson(fileItemIterator, limit, delimiter);
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

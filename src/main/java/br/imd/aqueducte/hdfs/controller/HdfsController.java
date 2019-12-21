package br.imd.aqueducte.hdfs.controller;

import br.imd.aqueducte.hdfs.HdfsService;
import br.imd.aqueducte.models.response.Response;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;
import java.util.Optional;

@RestController
@RequestMapping("/sync/hdfs")
@CrossOrigin(origins = "*")
public class HdfsController {
    private HdfsService hdfsService;

    HdfsController() {
        this.hdfsService = new HdfsService();
    }

    @PostMapping(value = "/directory")
    public ResponseEntity<Response<String>> createDirectoryService(@RequestParam Optional<String> name) {
        Response<String> response = new Response<>();

        if (name.isEmpty()) {
            response.getErrors().add("Nome do diretório é requerido");
            return ResponseEntity.badRequest().body(response);
        }

        try {
            hdfsService.createDirectory(name.get());
            response.setData("Diretório criado com sucesso!");
        } catch (IOException e) {
            e.printStackTrace();
            response.getErrors().add(e.getMessage());
            return ResponseEntity.badRequest().body(response);
        }
        return ResponseEntity.ok(response);
    }


}

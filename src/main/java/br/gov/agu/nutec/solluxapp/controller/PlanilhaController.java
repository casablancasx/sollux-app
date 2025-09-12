package br.gov.agu.nutec.solluxapp.controller;

import br.gov.agu.nutec.solluxapp.service.PlanilhaService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import java.util.Map;

@RestController
@RequestMapping("/planilhas")
@RequiredArgsConstructor
public class PlanilhaController {

    private final PlanilhaService planilhaService;


    @PostMapping("/importar")
    public ResponseEntity<Map<String, String>> importarPlanilha(@RequestParam("file") MultipartFile file) {

        if (file.isEmpty()) {
            return ResponseEntity.badRequest().body(Map.of("message", "Arquivo vazio"));
        }

        var response = planilhaService.importarPlanilha(file);
        return ResponseEntity.ok(response);
    }

}

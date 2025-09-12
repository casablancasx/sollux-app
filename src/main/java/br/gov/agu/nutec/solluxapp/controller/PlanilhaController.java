package br.gov.agu.nutec.solluxapp.controller;

import br.gov.agu.nutec.solluxapp.service.PlanilhaService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.messaging.handler.annotation.Header;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.Map;

@RestController
@RequestMapping("/planilhas")
@RequiredArgsConstructor
public class PlanilhaController {

    private final PlanilhaService planilhaService;


    @PostMapping("/importar")
    public ResponseEntity<Map<String, Object>> importarPlanilha(@RequestParam("file") MultipartFile file, @RequestHeader("Authorization") String token) throws Exception {
        var response = planilhaService.importarPlanilha(file,token);
        return ResponseEntity.ok(response);
    }

}

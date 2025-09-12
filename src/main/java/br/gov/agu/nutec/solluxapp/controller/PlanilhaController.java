package br.gov.agu.nutec.solluxapp.controller;

import br.gov.agu.nutec.solluxapp.dto.PlanilhaResponseDTO;
import br.gov.agu.nutec.solluxapp.service.PlanilhaService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;


@RestController
@RequestMapping("/planilhas")
@RequiredArgsConstructor
public class PlanilhaController {

    private final PlanilhaService planilhaService;


    @PostMapping("/importar")
    public ResponseEntity<PlanilhaResponseDTO> importarPlanilha(final @RequestParam("file") MultipartFile file, final @RequestHeader("Authorization") String token) throws Exception {
        PlanilhaResponseDTO response = planilhaService.importarPlanilha(file,token);
        return ResponseEntity.ok(response);
    }

}

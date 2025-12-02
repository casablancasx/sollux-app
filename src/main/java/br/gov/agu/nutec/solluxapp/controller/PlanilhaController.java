package br.gov.agu.nutec.solluxapp.controller;

import br.gov.agu.nutec.solluxapp.dto.PageResponse;
import br.gov.agu.nutec.solluxapp.dto.PlanilhaDTO;
import br.gov.agu.nutec.solluxapp.dto.PlanilhaResponseDTO;
import br.gov.agu.nutec.solluxapp.service.PlanilhaService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;


@RestController
@RequestMapping("/planilha")
@RequiredArgsConstructor
public class PlanilhaController {

    private final PlanilhaService planilhaService;


    @PostMapping("/importar")
    public synchronized ResponseEntity<PlanilhaDTO> importarPlanilha(final @RequestParam("file") MultipartFile file, @RequestHeader("Authorization") String token) throws Exception {
        token = token.replace("Bearer ", "");
        PlanilhaDTO response = planilhaService.importarPlanilha(file,token);
        return ResponseEntity.ok(response);
    }

    @GetMapping
    public ResponseEntity<PageResponse<PlanilhaResponseDTO>> listarPlanilhasPaginadas(@RequestParam(defaultValue = "0") int page, @RequestParam(defaultValue = "10") int size){
        return ResponseEntity.ok(planilhaService.listarPlanilhasPaginadas(page, size));
    }
}

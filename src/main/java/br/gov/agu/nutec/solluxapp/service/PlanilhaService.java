package br.gov.agu.nutec.solluxapp.service;

import br.gov.agu.nutec.solluxapp.dto.*;
import br.gov.agu.nutec.solluxapp.entity.PlanilhaEntity;
import br.gov.agu.nutec.solluxapp.entity.UsuarioEntity;
import br.gov.agu.nutec.solluxapp.enums.Role;
import br.gov.agu.nutec.solluxapp.exceptions.ResourceNotFoundException;
import br.gov.agu.nutec.solluxapp.exceptions.UserUnauthorizedException;
import br.gov.agu.nutec.solluxapp.reader.PlanilhaReader;
import br.gov.agu.nutec.solluxapp.repository.PlanilhaRepository;
import br.gov.agu.nutec.solluxapp.repository.UsuarioRepository;
import br.gov.agu.nutec.solluxapp.util.FileHashUtil;
import br.gov.agu.nutec.solluxapp.util.TokenUtil;
import br.gov.agu.nutec.solluxapp.validator.PlanilhaValidator;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.List;


@Service
@RequiredArgsConstructor
public class PlanilhaService {

    private final PlanilhaRepository planilhaRepository;
    private final UsuarioRepository usuarioRepository;
    private final PlanilhaReader planilhaReader;
    private final AudienciaAsyncService audienciaAsyncService;
    private final PlanilhaValidator validator;

    @Value("${app.timezone}")
    private String timeZone;


    public PlanilhaDTO importarPlanilha(final MultipartFile file, String token) throws Exception {

        PlanilhaEntity planilha = new PlanilhaEntity();
        planilha.setNomeArquivo(file.getOriginalFilename());
        planilha.setDataUpload(LocalDateTime.now(ZoneId.of(timeZone)));
        UsuarioEntity usuario = getUsuario(token);
        planilha.setUsuario(usuario);
        planilha.setProcessamentoConcluido(false);

        String hash = FileHashUtil.getFileHash(file, "SHA-256");
        planilha.setHash(hash);

        validator.validarArquivo(file, hash);
        planilhaRepository.save(planilha);

        List<AudienciaDTO> audiencias = planilhaReader.lerPlanilha(file,token);

        audienciaAsyncService.processarAudienciasAsync(audiencias, planilha, token);

        return new PlanilhaDTO(
                "Audiencias importadas com sucesso",
                file.getOriginalFilename(),
                usuario.getNome(),
                hash
        );
    }


    public PageResponse<PlanilhaResponseDTO> listarPlanilhasPaginadas(int page, int size){
        Pageable pageable = PageRequest.of(page, size, Sort.by(Sort.Direction.DESC, "dataUpload"));
        Page<PlanilhaEntity> planilhasPage = planilhaRepository.findAll(pageable);

        List<PlanilhaResponseDTO> content = planilhasPage.getContent().stream()
                .map(this::toPlanilhaResponseDTO)
                .toList();

        return new PageResponse<>(
                content,
                planilhasPage.getNumber(),
                planilhasPage.getSize(),
                planilhasPage.getTotalElements(),
                planilhasPage.getTotalPages()
        );
    }

    private PlanilhaResponseDTO toPlanilhaResponseDTO(PlanilhaEntity planilha) {
        PlanilhaResponseDTO dto = new PlanilhaResponseDTO();
        dto.setAdicionadaPor(planilha.getUsuario().getNome());
        dto.setNomeArquivo(planilha.getNomeArquivo());
        dto.setProcessamentoConcluido(planilha.isProcessamentoConcluido());
        return dto;
    }





    private void salvarPlanilha(final MultipartFile file,final String hash,final UsuarioEntity usuario) {
        PlanilhaEntity planilha = new PlanilhaEntity();
        planilha.setNomeArquivo(file.getOriginalFilename());
        planilha.setHash(hash);
        planilha.setDataUpload(LocalDateTime.now(ZoneId.of(timeZone)));
        planilha.setUsuario(usuario);
        planilhaRepository.save(planilha);
    }

    private UsuarioEntity getUsuario(final String token) {

        long sapiensId = TokenUtil.getSapiensIdFromToken(token);

        UsuarioEntity usuario = usuarioRepository.findBySapiensId(sapiensId)
                .orElseThrow(() -> new ResourceNotFoundException("Nenhum usuário encontrado para o sapiensId: " + sapiensId));

        if (usuario.getRole() != Role.ADMIN) {
            throw new UserUnauthorizedException("Usuário não autorizado a realizar a ação de importar planilha.");
        }

        return usuario;
    }


}

package br.gov.agu.nutec.solluxapp.service;

import br.gov.agu.nutec.solluxapp.dto.AudienciaDTO;
import br.gov.agu.nutec.solluxapp.dto.PlanilhaResponseDTO;
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
    private final AudienciaService audienciaService;
    private final PlanilhaValidator validator;

    @Value("${app.timezone}")
    private String timeZone;


    public PlanilhaResponseDTO importarPlanilha(final MultipartFile file, final String token) throws Exception {

        UsuarioEntity usuario = getUsuario(token);

        String hash = FileHashUtil.getFileHash(file, "SHA-256");

        validator.validarArquivo(file, hash);

        List<AudienciaDTO> audiencias = planilhaReader.lerPlanilha(file);

        audienciaService.enviarAudiencias(audiencias);

        salvarPlanilha(file, hash, usuario);

        return new PlanilhaResponseDTO(
                "Audiencias importadas com sucesso",
                file.getOriginalFilename(),
                usuario.getNome(),
                hash,
                audiencias.size()
        );
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

package br.gov.agu.nutec.solluxapp.service;

import br.gov.agu.nutec.solluxapp.dto.AudienciaDTO;
import br.gov.agu.nutec.solluxapp.entity.PlanilhaEntity;
import br.gov.agu.nutec.solluxapp.entity.UsuarioEntity;
import br.gov.agu.nutec.solluxapp.enums.Role;
import br.gov.agu.nutec.solluxapp.exceptions.ResourceNotFoundException;
import br.gov.agu.nutec.solluxapp.exceptions.UserUnauthorizedException;
import br.gov.agu.nutec.solluxapp.mapper.AudienciaRowMapper;
import br.gov.agu.nutec.solluxapp.producer.AudienciaProducer;
import br.gov.agu.nutec.solluxapp.reader.PlanilhaReader;
import br.gov.agu.nutec.solluxapp.repository.PlanilhaRepository;
import br.gov.agu.nutec.solluxapp.repository.UsuarioRepository;
import br.gov.agu.nutec.solluxapp.util.FileHashUtil;
import br.gov.agu.nutec.solluxapp.util.TokenUtil;
import br.gov.agu.nutec.solluxapp.validator.PlanilhaValidator;
import lombok.RequiredArgsConstructor;
import org.apache.poi.ss.usermodel.Sheet;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.List;
import java.util.Map;
import java.util.Objects;

@Service
@RequiredArgsConstructor
public class PlanilhaService {

    private final PlanilhaRepository planilhaRepository;
    private final UsuarioRepository usuarioRepository;
    private final AudienciaProducer audienciaProducer;
    private final PlanilhaReader planilhaReader;
    private final PlanilhaValidator validator;


    public Map<String, Object> importarPlanilha(MultipartFile file, String token) throws Exception {


        String hash = FileHashUtil.getFileHash(file, "SHA-256");

        validator.validarArquivo(file, hash);

        List<AudienciaDTO> audiencias = planilhaReader.lerPlanilha(file);

        UsuarioEntity usuario = getUsuario(token);

        audiencias.forEach(audienciaProducer::enviarAudiencia);

        salvarPlanilha(file, hash, usuario);

        return Map.of(
                "message", "Planilha importada com sucesso",
                "arquivo", Objects.requireNonNull(file.getOriginalFilename()),
                "hash", hash,
                "usuario", usuario.getNome(),
                "total_audiencias", audiencias.size()
        );
    }



    private void salvarPlanilha(MultipartFile file, String hash, UsuarioEntity usuario) {
        PlanilhaEntity planilha = new PlanilhaEntity();
        planilha.setNomeArquivo(file.getOriginalFilename());
        planilha.setHash(hash);
        planilha.setDataUpload(LocalDateTime.now(ZoneId.systemDefault()));
        planilha.setUsuario(usuario);
        planilhaRepository.save(planilha);
    }

    private UsuarioEntity getUsuario(String token) {
        long sapiensId = TokenUtil.getSapiensIdFromToken(token);
        UsuarioEntity usuario = usuarioRepository.findBySapiensId(sapiensId)
                .orElseThrow(() -> new ResourceNotFoundException("Nenhum usuário encontrado para o sapiensId: " + sapiensId));

        if (usuario.getRole() != Role.ADMIN) {
            throw new UserUnauthorizedException("Usuário não autorizado a realizar a ação de importar planilha.");
        }

        return usuario;
    }


}

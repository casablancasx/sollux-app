package br.gov.agu.nutec.solluxapp.service;

import br.gov.agu.nutec.solluxapp.dto.AudienciaDTO;
import br.gov.agu.nutec.solluxapp.entity.PlanilhaEntity;
import br.gov.agu.nutec.solluxapp.entity.UsuarioEntity;
import br.gov.agu.nutec.solluxapp.enums.Role;
import br.gov.agu.nutec.solluxapp.mapper.AudienciaRowMapper;
import br.gov.agu.nutec.solluxapp.producer.AudienciaProducer;
import br.gov.agu.nutec.solluxapp.repository.PlanilhaRepository;
import br.gov.agu.nutec.solluxapp.repository.UsuarioRepository;
import br.gov.agu.nutec.solluxapp.util.FileHashUtil;
import br.gov.agu.nutec.solluxapp.util.TokenUtil;
import lombok.RequiredArgsConstructor;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.ss.usermodel.WorkbookFactory;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.io.InputStream;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class PlanilhaService {

    private final AudienciaRowMapper audienciaRowMapper;
    private final PlanilhaRepository planilhaRepository;
    private final UsuarioRepository usuarioRepository;
    private final AudienciaProducer audienciaProducer;


    public Map<String, String> importarPlanilha(MultipartFile file, String token) throws Exception {


        String hash = FileHashUtil.getFileHash(file, "SHA-256");
        validarArquivo(file, hash);
        UsuarioEntity usuario = getUsuario(token);
        lerPlanilha(file);
        salvarPlanilha(file, hash, usuario);


        return Map.of("message", "Planilha importada com sucesso");
    }

    private void lerPlanilha(MultipartFile file) {
        try (InputStream is = file.getInputStream(); Workbook workbook = WorkbookFactory.create(is)) {

            Sheet sheet = workbook.getSheetAt(0);
            validarPlanilha(sheet);

            List<AudienciaDTO> audiencias = new ArrayList<>();
            for (Row row : sheet) {
                if (row == null || row.getRowNum() == 0) continue;
                audiencias.add(audienciaRowMapper.getAudienciaRow(row));
            }

            audiencias.forEach(audienciaProducer::enviarAudiencia);

        } catch (IOException e) {
            throw new RuntimeException(e);
        }
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
        UsuarioEntity usuario = usuarioRepository.findById(sapiensId).orElseThrow(() -> new IllegalArgumentException("Usuário não encontrado."));

        if (usuario.getRole() != Role.ADMIN) {
            throw new RuntimeException("Acesso negado. Usuário não é administrador.");
        }

        return usuario;
    }


    private void validarArquivo(MultipartFile file, String hash) {

        if (file.isEmpty()) {
            throw new IllegalArgumentException("Arquivo vazio.");
        }

        if (!file.getOriginalFilename().endsWith(".xls") && !file.getOriginalFilename().endsWith(".xlsx")) {
            throw new IllegalArgumentException("Formato de arquivo inválido. Apenas arquivos .xls são aceitos.");
        }

        if (planilhaRepository.existsByHash(hash)) {
            throw new IllegalArgumentException("Arquivo já importado anteriormente.");
        }


    }

    private void validarPlanilha(Sheet sheet) {
        int numeroDeColunas = sheet.getRow(0).getPhysicalNumberOfCells();

        if (numeroDeColunas != 10) {
            throw new IllegalArgumentException("Planilha inválida. Número de colunas incorreto.");
        }
    }


}

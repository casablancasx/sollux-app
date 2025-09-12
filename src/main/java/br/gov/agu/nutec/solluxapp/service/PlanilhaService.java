package br.gov.agu.nutec.solluxapp.service;

import br.gov.agu.nutec.solluxapp.dto.AudienciaDTO;
import br.gov.agu.nutec.solluxapp.entity.PlanilhaEntity;
import br.gov.agu.nutec.solluxapp.entity.UsuarioEntity;
import br.gov.agu.nutec.solluxapp.mapper.AudienciaRowMapper;
import br.gov.agu.nutec.solluxapp.repository.PlanilhaRepository;
import br.gov.agu.nutec.solluxapp.repository.UsuarioRepository;
import br.gov.agu.nutec.solluxapp.util.FileHashUtil;
import br.gov.agu.nutec.solluxapp.util.TokenUtil;
import lombok.RequiredArgsConstructor;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.io.InputStream;
import java.time.LocalDateTime;
import java.util.Map;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class PlanilhaService {

    private final AudienciaRowMapper audienciaRowMapper;
    private final PlanilhaRepository planilhaRepository;
    private final UsuarioRepository usuarioRepository;


    public Map<String, String> importarPlanilha(MultipartFile file, String token) throws Exception {


        try (InputStream is = file.getInputStream(); Workbook workbook = new HSSFWorkbook(is);) {

            String nomeArquivo = file.getOriginalFilename();
            String hash = FileHashUtil.getFileHash(file, "MD5");
            validarArquivo(nomeArquivo, hash);

            Sheet sheet = workbook.getSheetAt(0);
            validarPlanilha(sheet);

            for (Row row : sheet) {
                if (row == null || row.getRowNum() == 0) {
                    break;
                }
                AudienciaDTO audiencia = audienciaRowMapper.getAudienciaRow(row);
            }

            PlanilhaEntity planilhaEntity = new PlanilhaEntity();
            planilhaEntity.setNomeArquivo(nomeArquivo);
            planilhaEntity.setHash(hash);
            planilhaEntity.setDataUpload(LocalDateTime.now(java.time.ZoneId.of("America/Sao_Paulo")));
            long sapiensId = TokenUtil.getSapiensIdFromToken(token);
            Optional<UsuarioEntity> usuarioOpt = usuarioRepository.findById(sapiensId);

            if (usuarioOpt.isEmpty()) {
                throw new IllegalArgumentException("Usuário não encontrado.");
            }

            planilhaEntity.setUsuario(usuarioOpt.get());
            planilhaRepository.save(planilhaEntity);

        } catch (IOException e) {
            throw new RuntimeException(e);
        }

        return Map.of("message", "Planilha importada com sucesso");
    }


    private void validarArquivo(String nomeArquivo, String hash) {


        if (nomeArquivo == null || !nomeArquivo.endsWith(".xls")) {
            throw new IllegalArgumentException("Arquivo inválido. Apenas arquivos .xls são suportados.");
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

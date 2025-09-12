package br.gov.agu.nutec.solluxapp.service;

import br.gov.agu.nutec.solluxapp.dto.AudienciaDTO;
import br.gov.agu.nutec.solluxapp.mapper.AudienciaRowMapper;
import br.gov.agu.nutec.solluxapp.repository.PlanilhaRepository;
import br.gov.agu.nutec.solluxapp.util.FileHashUtil;
import lombok.RequiredArgsConstructor;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@Service
@RequiredArgsConstructor
public class PlanilhaService {

    private final AudienciaRowMapper audienciaRowMapper;
    private final PlanilhaRepository planilhaRepository;


    public Map<String, String> importarPlanilha(MultipartFile file) throws Exception {

        validarArquivo(file);
        List<AudienciaDTO> lista = new ArrayList<>();

        try (InputStream is = file.getInputStream();
             Workbook workbook = new HSSFWorkbook(is);) {
            Sheet sheet = workbook.getSheetAt(0);
            validarPlanilha(sheet);


            for (Row row : sheet) {

                if (row == null || row.getRowNum() == 0) {
                    break;
                }
                AudienciaDTO audiencia = audienciaRowMapper.getAudienciaRow(row);
                lista.add(audiencia);
            }

        } catch (IOException e) {
            throw new RuntimeException(e);
        }

        return Map.of("message", "Planilha importada com sucesso");
    }


    private void validarArquivo(MultipartFile file) throws Exception {

        String nomeArquivo = file.getOriginalFilename();
        String hash = FileHashUtil.getFileHash(file, "MD5");


        if (planilhaRepository.existsByHash(hash)) {
            throw new IllegalArgumentException("Arquivo já importado anteriormente.");
        }


        if (file.isEmpty()) {
            throw new IllegalArgumentException("Arquivo vazio");
        }


        if (nomeArquivo == null || !nomeArquivo.endsWith(".xls")) {
            throw new IllegalArgumentException("Arquivo inválido. Apenas arquivos .xls são suportados.");
        }

    }

    private void validarPlanilha(Sheet sheet) {
        int numeroDeColunas = sheet.getRow(0).getPhysicalNumberOfCells();

        if (numeroDeColunas != 10) {
            throw new IllegalArgumentException("Planilha inválida. Número de colunas incorreto.");
        }
    }


}

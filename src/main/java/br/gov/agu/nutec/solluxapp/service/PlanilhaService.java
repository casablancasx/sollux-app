package br.gov.agu.nutec.solluxapp.service;

import br.gov.agu.nutec.solluxapp.dto.AudienciaDTO;
import br.gov.agu.nutec.solluxapp.mapper.AudienciaRowMapper;
import br.gov.agu.nutec.solluxapp.util.FileHashUtil;
import lombok.RequiredArgsConstructor;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
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


    public Map<String, String> importarPlanilha(MultipartFile file) {

        validarArquivo(file);
        List<AudienciaDTO> lista = new ArrayList<>();

        try (InputStream is = file.getInputStream();
             Workbook workbook = new XSSFWorkbook(is);) {
            Sheet sheet = workbook.getSheetAt(0);
            validarPlanilha(sheet);

            for (int i = 1; i < sheet.getLastRowNum(); i++) {
                Row row = sheet.getRow(i);
                if (row == null) continue;
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
        String hashArquivo = FileHashUtil.getFileHash(file, "MD5");



        if (file.isEmpty()) {
            throw new IllegalArgumentException("Arquivo vazio");
        }


        if (nomeArquivo == null || !nomeArquivo.endsWith(".xlsx")) {
            throw new IllegalArgumentException("Arquivo inválido. Apenas arquivos .xlsx são suportados.");
        }

    }

    private void validarPlanilha(Sheet sheet) {
        int numeroDeColunas = sheet.getRow(0).getPhysicalNumberOfCells();

        if (numeroDeColunas != 10) {
            throw new IllegalArgumentException("Planilha inválida. Número de colunas incorreto.");
        }
    }


}

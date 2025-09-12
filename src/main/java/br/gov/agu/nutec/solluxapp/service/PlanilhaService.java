package br.gov.agu.nutec.solluxapp.service;

import br.gov.agu.nutec.solluxapp.dto.AudienciaDTO;
import br.gov.agu.nutec.solluxapp.mapper.AudienciaRowMapper;
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

        List<AudienciaDTO> lista = new ArrayList<>();

        try (InputStream is = file.getInputStream();
             Workbook workbook = new XSSFWorkbook(is);) {

            Sheet sheet = workbook.getSheetAt(0);

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


}

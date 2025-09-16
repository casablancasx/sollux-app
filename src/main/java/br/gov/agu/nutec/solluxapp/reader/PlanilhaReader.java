package br.gov.agu.nutec.solluxapp.reader;

import br.gov.agu.nutec.solluxapp.dto.AudienciaDTO;
import br.gov.agu.nutec.solluxapp.exceptions.PlanilhaException;
import br.gov.agu.nutec.solluxapp.mapper.AudienciaRowMapper;
import br.gov.agu.nutec.solluxapp.validator.PlanilhaValidator;
import lombok.RequiredArgsConstructor;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.ss.usermodel.WorkbookFactory;
import org.springframework.stereotype.Component;
import org.springframework.web.multipart.MultipartFile;

import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

@Component
@RequiredArgsConstructor
public class PlanilhaReader {

    private final AudienciaRowMapper audienciaRowMapper;
    private final PlanilhaValidator validator;

    public List<AudienciaDTO> lerPlanilha(final MultipartFile file) {
        try (InputStream is = file.getInputStream(); Workbook workbook = WorkbookFactory.create(is)) {
            Sheet sheet = workbook.getSheetAt(0);

            validator.validarPlanilha(sheet);

            List<AudienciaDTO> audiencias = new ArrayList<>();
            for (Row row : sheet) {

                if (row == null || row.getRowNum() == 0) continue;
                if (row.getCell(0) == null) break;

                AudienciaDTO audienciaDTO = audienciaRowMapper.getAudienciaRow(row);
                audiencias.add(audienciaDTO);
            }

            return audiencias;
        } catch (Exception e) {
            throw new PlanilhaException("Erro ao ler a planilha");
        }
    }
}
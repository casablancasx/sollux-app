package br.gov.agu.nutec.solluxapp.reader;

import br.gov.agu.nutec.solluxapp.dto.AudienciaDTO;
import br.gov.agu.nutec.solluxapp.dto.AudienciaMessage;
import br.gov.agu.nutec.solluxapp.enums.TipoContestacao;
import br.gov.agu.nutec.solluxapp.exceptions.PlanilhaException;
import br.gov.agu.nutec.solluxapp.mapper.AudienciaRowMapper;
import br.gov.agu.nutec.solluxapp.producer.AudienciaProducer;
import br.gov.agu.nutec.solluxapp.service.ContestacaoService;
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
    private final AudienciaProducer producer;
    private final ContestacaoService  contestacaoService;
    private final PlanilhaValidator validator;

    public void lerPlanilha(final MultipartFile file, String token) {
        try (InputStream is = file.getInputStream(); Workbook workbook = WorkbookFactory.create(is)) {
            Sheet sheet = workbook.getSheetAt(0);

            validator.validarPlanilha(sheet);

            for (Row row : sheet) {
                if (row == null || row.getRowNum() == 0) continue;
                if (row.getCell(0) == null) break;

                AudienciaDTO audienciaDTO = audienciaRowMapper.getAudienciaRow(row);
                TipoContestacao tipoContestacao = contestacaoService.buscarTipoConstestacao(audienciaDTO.getCnj(),token);
                audienciaDTO.setTipoContestacao(tipoContestacao);
                producer.enviarAudiencia(new AudienciaMessage(audienciaDTO));
            }
        } catch (Exception e) {
            throw new PlanilhaException("Erro ao ler a planilha");
        }
    }
}
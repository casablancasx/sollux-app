package br.gov.agu.nutec.solluxapp.validator;

import br.gov.agu.nutec.solluxapp.exceptions.ResourceAlreadyExistsException;
import br.gov.agu.nutec.solluxapp.repository.PlanilhaRepository;
import lombok.RequiredArgsConstructor;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.springframework.stereotype.Component;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@Component
@RequiredArgsConstructor
public class PlanilhaValidator {

    private static final List<String> EXPECTED_COLUMNS = List.of(
            "Data/Hora",
            "Processo",
            "Órgão Julgador",
            "Partes",
            "Classe Judicial",
            "Advogados",
            "Assunto",
            "Tipo",
            "Sala",
            "Situação"
    );

    private final PlanilhaRepository planilhaRepository;

    public void validarArquivo(final MultipartFile file, final String hash) {
        if (file.isEmpty()) {
            throw new IllegalArgumentException("Arquivo vazio.");
        }

        if (!file.getOriginalFilename().endsWith(".xls")
                && !file.getOriginalFilename().endsWith(".xlsx")) {
            throw new IllegalArgumentException("Formato inválido. Apenas .xls ou .xlsx são suportados.");
        }

        if (planilhaRepository.existsByHash(hash)) {
            throw new ResourceAlreadyExistsException("Arquivo já importado anteriormente.");
        }
    }

    public void validarPlanilha(final Sheet sheet) {
        Row header = sheet.getRow(0);
        for (int i = 0; i < EXPECTED_COLUMNS.size(); i++) {
            String cellValue = header.getCell(i).getStringCellValue().trim();
            if (!EXPECTED_COLUMNS.get(i).equalsIgnoreCase(cellValue)) {
                throw new IllegalArgumentException(
                        String.format("Planilha inválida. Coluna esperada: %s mas encontrada: %s",
                                EXPECTED_COLUMNS.get(i), cellValue)
                );
            }
        }
    }
}

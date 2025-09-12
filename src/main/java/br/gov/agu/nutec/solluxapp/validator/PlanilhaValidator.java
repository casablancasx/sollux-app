package br.gov.agu.nutec.solluxapp.validator;

import br.gov.agu.nutec.solluxapp.exceptions.PlanilhaMapperException;
import br.gov.agu.nutec.solluxapp.exceptions.ResourceAlreadyExistsException;
import br.gov.agu.nutec.solluxapp.repository.PlanilhaRepository;
import lombok.RequiredArgsConstructor;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.springframework.stereotype.Component;
import org.springframework.web.multipart.MultipartFile;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

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

        if (!file.getOriginalFilename().endsWith(".xls")  || !file.getOriginalFilename().endsWith(".xlsx")) {
            throw new IllegalArgumentException("Formato inválido. Apenas .xls ou .xlsx são suportados.");
        }

        if (planilhaRepository.existsByHash(hash)) {
            throw new ResourceAlreadyExistsException("Arquivo já importado anteriormente.");
        }
    }

    public void validarPlanilha(final Sheet sheet) {
        Row header = sheet.getRow(0);

        if (header == null) {
            throw new PlanilhaMapperException("A planilha não possui cabeçalho.");
        }

        Set<String> encontradas = new HashSet<>();
        for (Cell cell : header) {
            encontradas.add(safeCellValue(cell).toLowerCase());
        }

        List<String> faltando = EXPECTED_COLUMNS.stream()
                .filter(col -> !encontradas.contains(col.toLowerCase()))
                .toList();

        if (!faltando.isEmpty()) {
            throw new PlanilhaMapperException("Colunas faltando: " + String.join(", ", faltando));
        }
    }

    private String safeCellValue(Cell cell) {
        return (cell == null) ? "" : cell.getStringCellValue().trim();
    }
}

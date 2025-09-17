package br.gov.agu.nutec.solluxapp.mapper;


import br.gov.agu.nutec.solluxapp.dto.AudienciaDTO;
import br.gov.agu.nutec.solluxapp.enums.Prioridade;
import br.gov.agu.nutec.solluxapp.enums.Turno;
import br.gov.agu.nutec.solluxapp.enums.Uf;
import br.gov.agu.nutec.solluxapp.repository.AdvogadoRepository;
import lombok.RequiredArgsConstructor;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;
import org.springframework.stereotype.Component;
import static br.gov.agu.nutec.solluxapp.enums.Turno.TARDE;
import static br.gov.agu.nutec.solluxapp.enums.Turno.MANHA;
import  static br.gov.agu.nutec.solluxapp.enums.Prioridade.ALTA;
import  static br.gov.agu.nutec.solluxapp.enums.Prioridade.NORMAL;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.List;

@Component
@RequiredArgsConstructor
public  class AudienciaRowMapper {


    private final AdvogadoRepository advogadoRepository;
    private static final String POLO_PASSIVO = "INSTITUTO NACIONAL DO SEGURO SOCIAL - INSS";
    private static final String RURAL = "Rural";
    private static final String SALARIO_MATERNIDADE = "Salário-Maternidade (Art. 71/73)";
    private static final DateTimeFormatter DATE_FORMAT = DateTimeFormatter.ofPattern("dd/MM/yyyy");

    public  AudienciaDTO getAudienciaRow(Row row) {

        LocalDate data  = getDataAudiencia(row.getCell(0));
        String hora = getHoraAudiencia(row.getCell(0));
        String cnj = row.getCell(1).getStringCellValue();
        String orgaoJulgador = row.getCell(2).getStringCellValue();
        String poloAtivo = getPoloAtivo(row.getCell(3));
        String classeJudicial = row.getCell(4).getStringCellValue();
        List<String> advogados = getAdvogados(row.getCell(5));
        String assunto =  getAssunto(row.getCell(6));
        String tipo =  row.getCell(7).getStringCellValue();
        String sala =  row.getCell(8).getStringCellValue();
        String situacao =  row.getCell(9).getStringCellValue();
        Turno turno = getTurno(hora);
        Prioridade prioridade = getPrioridade(assunto, advogados);
        Uf uf = getUfFromOrgaoJulgador(orgaoJulgador);

        return new AudienciaDTO(
                cnj,
                data,
                hora,
                turno,
                sala,
                orgaoJulgador,
                poloAtivo,
                POLO_PASSIVO,
                classeJudicial,
                advogados,
                assunto,
                tipo,
                situacao,
                prioridade,
                uf
        );

    }

    private Uf getUfFromOrgaoJulgador(String orgaoJulgador) {
        String ultimaDuasLetras = orgaoJulgador.substring(orgaoJulgador.length() - 2);
        return Uf.valueOf(ultimaDuasLetras);
    }

    private Prioridade getPrioridade(String assunto, List<String> advogados) {

        boolean impeditivoAdvogado = advogadoRepository.existsByNomeIn(advogados);
        if (assunto.equals(SALARIO_MATERNIDADE) && impeditivoAdvogado) {
            return ALTA;
        }
        return NORMAL;
    }


    private String getAssunto(final Cell cell) {
        //Lógica criada pois na planilha, as audiencias que possuem assunto "Rural"
        // sem ser "Aposentadoria Rural (Art. 48/51)" são na verdade Salario Maternidade
        String assunto = cell.getStringCellValue().trim();
        if (assunto.equals(RURAL.trim())) {
            return SALARIO_MATERNIDADE;
        }
        return assunto;
    }


    private static Turno getTurno(final String hora) {
        int hour = Integer.parseInt(hora.split(":")[0]);
        return hour < 13 ? MANHA : TARDE;
    }


    private static List<String> getAdvogados(final Cell advogadosCell) {
        String advogadosStr = advogadosCell.getStringCellValue();
        return List.of(advogadosStr.split(","));
    }

    private static String getPoloAtivo(final Cell salaCell) {
        return salaCell.getStringCellValue().replace(POLO_PASSIVO, "");
    }

    private static LocalDate getDataAudiencia(final Cell dataHoraCell) {
        String dataHoraStr = dataHoraCell.getStringCellValue();
        String[] partes = dataHoraStr.split(" ");

        return LocalDate.parse(partes[0], DATE_FORMAT);
    }

    private static String getHoraAudiencia(final Cell dataHoraCell) {

        String dataHoraStr = dataHoraCell.getStringCellValue();
        String[] partes = dataHoraStr.split(" ");
        return partes.length > 1 ? partes[1] : "";
    }

}

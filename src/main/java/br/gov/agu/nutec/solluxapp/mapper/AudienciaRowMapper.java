package br.gov.agu.nutec.solluxapp.mapper;


import br.gov.agu.nutec.solluxapp.dto.AudienciaDTO;
import br.gov.agu.nutec.solluxapp.enums.Turno;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;
import org.springframework.stereotype.Component;
import static br.gov.agu.nutec.solluxapp.enums.Turno.TARDE;
import static br.gov.agu.nutec.solluxapp.enums.Turno.MANHA;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.List;

@Component
public  class AudienciaRowMapper {

    private static final String POLO_PASSIVO = "INSTITUTO NACIONAL DO SEGURO SOCIAL - INSS";
    private static final DateTimeFormatter DATE_FORMAT = DateTimeFormatter.ofPattern("dd/MM/yyyy");

    public  AudienciaDTO getAudienciaRow(Row row) {

        LocalDate data  = getDataAudiencia(row.getCell(0));
        String hora = getHoraAudiencia(row.getCell(0));
        String cnj = row.getCell(1).getStringCellValue();
        System.out.println(cnj);
        String orgaoJulgador = row.getCell(2).getStringCellValue();
        String poloAtivo = getPoloAtivo(row.getCell(3));
        String classeJudicial = row.getCell(4).getStringCellValue();
        List<String> advogados = getAdvogados(row.getCell(5));
        String assunto =  row.getCell(6).getStringCellValue();
        String tipo =  row.getCell(7).getStringCellValue();
        String sala =  row.getCell(8).getStringCellValue();
        String situacao =  row.getCell(9).getStringCellValue();
        Turno turno = getTurno(hora);

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
                situacao
        );

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

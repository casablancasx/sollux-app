package br.gov.agu.nutec.solluxapp.dto;

import br.gov.agu.nutec.solluxapp.enums.TipoContestacao;
import br.gov.agu.nutec.solluxapp.enums.Turno;
import br.gov.agu.nutec.solluxapp.enums.Uf;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDate;
import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class AudienciaDTO {
    private String cnj;
    private LocalDate data;
    private String hora;
    private Turno turno;
    private String sala;
    private String orgaoJulgador;
    private String poloAtivo;
    private String poloPassivo;
    private String classeJudicial;
    private List<String> advogados;
    private String assunto;
    private String tipo;
    private String situacao;
    private Uf uf;
    private TipoContestacao tipoContestacao;
}



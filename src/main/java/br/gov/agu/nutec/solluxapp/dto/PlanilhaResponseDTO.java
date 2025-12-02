package br.gov.agu.nutec.solluxapp.dto;

import lombok.Data;

@Data
public class PlanilhaResponseDTO {

    private String adicionadaPor;
    private String nomeArquivo;
    private boolean processamentoConcluido;
}

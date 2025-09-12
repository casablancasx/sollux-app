package br.gov.agu.nutec.solluxapp.enums;

public enum Status {
    EM_ANDAMENTO("EM ANDAMENTO"),
    FINALIZADO("FINALIZADO");

    private final String descricao;

    Status(String descricao) {
        this.descricao = descricao;
    }
}

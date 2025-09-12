package br.gov.agu.nutec.solluxapp.enums;

public enum Prioridade {

    ALTA("ALTA"),
    NORMAL("NORMAL");

    private String descricao;

    Prioridade(String descricao) {
        this.descricao = descricao;
    }
}

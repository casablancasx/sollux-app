package br.gov.agu.nutec.solluxapp.enums;


public enum Turno {
    MANHA("MANHÃ"),
    TARDE("TARDE"),;


    private final String descricao;

    Turno(String descricao) {
        this.descricao = descricao;
    }

    public String getDescricao() {
        return descricao;
    }
}

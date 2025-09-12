package br.gov.agu.nutec.solluxapp.enums;

public enum Role {
    ADMIN("ADMIN"),
    USER("USER");

    private final String descricao;

    Role(String descricao) {
        this.descricao = descricao;
    }

}

package br.com.sol_do_amanhecer.shared.enums;

import lombok.Getter;

@Getter
public enum EMes {
    JANEIRO("Janeiro"),
    FEVEREIRO("Fevereiro"),
    MARCO("Março"),
    ABRIL("Abril"),
    MAIO("Maio"),
    JUNHO("Junho"),
    JULHO("Julho"),
    AGOSTO("Agosto"),
    SETEMBRO("Setembro"),
    OUTUBRO("Outubro"),
    NOVEMBRO("Novembro"),
    DEZEMBRO("Dezembro");

    private final String descricao;

    EMes(String descricao) {
        this.descricao = descricao;
    }

    public int getNumero() {
        return this.ordinal() + 1;
    }
}
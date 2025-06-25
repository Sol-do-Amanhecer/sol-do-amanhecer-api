package br.com.sol_do_amanhecer.shared.enums;

import lombok.Getter;

@Getter
public enum ETipoAcao {
    GESTANTE_CRIANCA("Apoio a Gestantes e Crianças"),
    SOCIAL_ALIMENTAR("Apoio Social e Alimentar"),
    ANIMAL("Bem-estar Animal"),
    IDOSO("Cuidados com Idosos"),
    COMEMORATIVA("Datas Comemorativas e Eventos"),
    AMBIENTE("Meio Ambiente"),
    SAUDE("Saúde e Bem-estar");

    public final String descricao;

    ETipoAcao(String descricao) {
        this.descricao = descricao;
    }
}

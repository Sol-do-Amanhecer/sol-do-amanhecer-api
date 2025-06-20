package br.com.sol_do_amanhecer.shared.enums;

import lombok.Getter;

@Getter
public enum ETipoAcao {
    gestanteCrianca("Apoio a Gestantes e Crianças"),
    socialAlimentar("Apoio Social e Alimentar"),
    animal("Bem-estar Animal"),
    idoso("Cuidados com Idosos"),
    comemorativa("Datas Comemorativas e Eventos"),
    inclusao("Inclusão Social"),
    ambiente("Meio Ambiente"),
    saude("Saúde e Bem-estar");

    public final String descricao;

    ETipoAcao(String descricao) {
        this.descricao = descricao;
    }


}

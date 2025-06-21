package br.com.sol_do_amanhecer.shared.enums;

import lombok.Getter;

@Getter
public enum EMeioDoacao {
    PIX("Pagamento via Pix"),
    BOLETO("Pagamento via boleto bancário"),
    TRANSFERENCIA("Transferência bancária"),
    DINHEIRO("Doação em espécie"),
    CARTAO_CREDITO("Pagamento por cartão de crédito"),
    CARTAO_DEBITO("Pagamento por cartão de débito");

    private final String descricao;

    EMeioDoacao(String descricao) {
        this.descricao = descricao;
    }
}
package br.com.sol_do_amanhecer.shared.constant;

public class PathsConstants {

    public static final String BASE_URL = "sol-do-amanhecer/api";

    public static final String AUTENTICACAO_BASE = "/autenticacao";
    public static final String AUTENTICACAO_LOGIN = AUTENTICACAO_BASE + "/login";
    public static final String AUTH_REFRESH_TOKEN = AUTENTICACAO_BASE + "/refresh-token/{usuario}";

    public static final String USUARIO_BASE = "/usuario";
    public static final String USUARIO_POR_ID = USUARIO_BASE + "/{id}";
    public static final String TODOS_USUARIOS = USUARIO_BASE + "/";
    public static final String CRIAR_USUARIO = USUARIO_BASE + "/criar";
    public static final String ATUALIZAR_USUARIO = USUARIO_BASE + "/atualizar/{id}";
    public static final String TROCAR_SENHA_USUARIO = USUARIO_BASE + "/{id}/trocar-senha";
    public static final String RESETAR_SENHA_USUARIO = USUARIO_BASE + "/resetar-senha/{username}";
    public static final String APAGAR_USUARIO = "/remover/{id}";

    public static final String PERMISSAO_BASE = "/permissao";
    public static final String PERMISSAO_POR_ID = PERMISSAO_BASE + "/{id}";
    public static final String TODAS_PERMISSOES = PERMISSAO_BASE + "/";
    public static final String CRIAR_PERMISSAO = PERMISSAO_BASE + "/criar";
    public static final String ATUALIZAR_PERMISSAO = PERMISSAO_BASE + "/atualizar/{id}";
    public static final String APAGAR_PERMISSAO = PERMISSAO_BASE + "/remover/{id}";

    public static final String VOLUNTARIO_BASE = "/voluntario";
    public static final String VOLUNTARIO_POR_ID = VOLUNTARIO_BASE + "/{id}";
    public static final String TODOS_VOLUNTARIOS = VOLUNTARIO_BASE + "/";
    public static final String CRIAR_VOLUNTARIO = VOLUNTARIO_BASE + "/criar";
    public static final String ATUALIZAR_VOLUNTARIO = VOLUNTARIO_BASE + "/atualizar/{id}";
    public static final String APAGAR_VOLUNTARIO = VOLUNTARIO_BASE + "/remover/{id}";
    public static final String STATUS_APROVACAO_VOLUNTARIO = VOLUNTARIO_BASE + "/voluntarios/{id}/status-aprovacao";

    public static final String ACAO_BASE = "/acao";
    public static final String ACAO_POR_ID = ACAO_BASE + "/{id}";
    public static final String TODAS_ACOES = ACAO_BASE + "/";
    public static final String CRIAR_ACAO = ACAO_BASE + "/criar";
    public static final String ATUALIZAR_ACAO = ACAO_BASE + "/atualizar/{id}";
    public static final String APAGAR_ACAO = ACAO_BASE + "/remover/{id}";

    public static final String DOACAO_BASE = "/doacao";
    public static final String DOACAO_POR_ID = DOACAO_BASE + "/{id}";
    public static final String TODAS_DOACOES = DOACAO_BASE + "/";
    public static final String CRIAR_DOACAO = DOACAO_BASE + "/criar";
    public static final String ATUALIZAR_DOACAO = DOACAO_BASE + "/atualizar/{id}";
    public static final String APAGAR_DOACAO = DOACAO_BASE + "/remover/{id}";

    public static final String PRESTACAO_BASE = "/prestacao";
    public static final String PRESTACAO_POR_ID = PRESTACAO_BASE + "/{id}";
    public static final String TODAS_PRESTACOES = PRESTACAO_BASE + "/";
    public static final String CRIAR_PRESTACAO = PRESTACAO_BASE + "/criar";
    public static final String ATUALIZAR_PRESTACAO = PRESTACAO_BASE + "/atualizar/{id}";
    public static final String APAGAR_PRESTACAO = PRESTACAO_BASE + "/remover/{id}";

    public static final String OBJETIVO_BASE = "/objetivo";
    public static final String OBJETIVO_POR_ID = OBJETIVO_BASE + "/{id}";
    public static final String TODOS_OBJETIVOS = OBJETIVO_BASE + "/";
    public static final String CRIAR_OBJETIVO = OBJETIVO_BASE + "/criar";
    public static final String ATUALIZAR_OBJETIVO = OBJETIVO_BASE + "/atualizar/{id}";
    public static final String APAGAR_OBJETIVO = OBJETIVO_BASE + "/remover/{id}";
}
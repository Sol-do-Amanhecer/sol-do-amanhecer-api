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
}
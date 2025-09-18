package br.com.sol_do_amanhecer.service.impl;

import br.com.sol_do_amanhecer.exception.UsuarioException;
import br.com.sol_do_amanhecer.model.dto.*;
import br.com.sol_do_amanhecer.model.entity.*;
import br.com.sol_do_amanhecer.repository.*;
import br.com.sol_do_amanhecer.util.EmailUtil;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.*;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@DisplayName("VoluntarioServiceImpl - Testes Completos")
class VoluntarioServiceImplTest {

    @Mock
    private VoluntarioRepository voluntarioRepository;
    @Mock
    private EmailRepository emailRepository;
    @Mock
    private TelefoneRepository telefoneRepository;
    @Mock
    private FormularioVoluntarioRepository formularioVoluntarioRepository;
    @Mock
    private UsuarioRepository usuarioRepository;
    @Mock
    private EmailUtil emailUtil;

    @InjectMocks
    private VoluntarioServiceImpl voluntarioService;

    private UUID voluntarioId;
    private Voluntario voluntario;
    private VoluntarioDTO voluntarioDTO;
    private List<EmailDTO> emailDTOList;
    private List<TelefoneDTO> telefoneDTOList;
    private FormularioVoluntarioDTO formularioDTO;
    private Email email;
    private Telefone telefone;
    private FormularioVoluntario formularioVoluntario;
    private Usuario usuario;
    private Pageable pageable;

    @BeforeEach
    void setUp() {
        voluntarioId = UUID.randomUUID();

        Endereco endereco = new Endereco();
        endereco.setLogradouro("Rua Teste");
        endereco.setNumero("123");
        endereco.setBairro("Centro");
        endereco.setCidade("São Paulo");
        endereco.setEstado("SP");
        endereco.setCep("12345-678");

        EnderecoDTO enderecoDTO = new EnderecoDTO();
        enderecoDTO.setLogradouro("Rua Teste");
        enderecoDTO.setNumero("123");
        enderecoDTO.setBairro("Centro");
        enderecoDTO.setCidade("São Paulo");
        enderecoDTO.setEstado("SP");
        enderecoDTO.setCep("12345-678");

        voluntario = new Voluntario();
        voluntario.setUuid(voluntarioId);
        voluntario.setNomeCompleto("João Silva Santos");
        voluntario.setDataNascimento(LocalDate.of(1990, 1, 1));
        voluntario.setEndereco(endereco);
        voluntario.setAtivo(true);
        voluntario.setAprovado(true);

        voluntarioDTO = new VoluntarioDTO();
        voluntarioDTO.setUuid(voluntarioId);
        voluntarioDTO.setNomeCompleto("João Silva Santos");
        voluntarioDTO.setDataNascimento(LocalDate.of(1990, 1, 1));
        voluntarioDTO.setEnderecoDTO(enderecoDTO);
        voluntarioDTO.setAtivo(true);

        EmailDTO emailDTO = new EmailDTO();
        emailDTO.setUuidVoluntario(voluntarioId);
        emailDTO.setEmail("joao@email.com");
        emailDTOList = List.of(emailDTO);

        TelefoneDTO telefoneDTO = new TelefoneDTO();
        telefoneDTO.setUuidVoluntario(voluntarioId);
        telefoneDTO.setDdd("11");
        telefoneDTO.setTelefone("999999999");
        telefoneDTOList = List.of(telefoneDTO);

        formularioDTO = new FormularioVoluntarioDTO();
        formularioDTO.setUuidVoluntario(voluntarioId);
        formularioDTO.setComoConheceu("Internet");
        formularioDTO.setMotivoVoluntariado("Ajudar pessoas");
        formularioDTO.setCienteTrabalhoVoluntario(true);
        formularioDTO.setDedicacaoVoluntariado(true);
        formularioDTO.setDisponibilidadeSemana("Fins de semana");
        formularioDTO.setCompromissoDivulgar(true);
        formularioDTO.setCompromissoAcao(true);
        formularioDTO.setDesejaCamisa(true);
        formularioDTO.setSobreMim("Sou uma pessoa dedicada");
        formularioDTO.setDataResposta(LocalDateTime.now());

        email = new Email();
        email.setUuid(UUID.randomUUID());
        email.setEmail("joao@email.com");
        email.setVoluntario(voluntario);

        telefone = new Telefone();
        telefone.setUuid(UUID.randomUUID());
        telefone.setDdd("11");
        telefone.setTelefone("999999999");
        telefone.setVoluntario(voluntario);

        formularioVoluntario = new FormularioVoluntario();
        formularioVoluntario.setUuid(UUID.randomUUID());
        formularioVoluntario.setVoluntario(voluntario);
        formularioVoluntario.setComoConheceu("Internet");
        formularioVoluntario.setMotivoVoluntariado("Ajudar pessoas");

        usuario = new Usuario();
        usuario.setUuid(UUID.randomUUID());
        usuario.setUsuario("joao.silva");
        usuario.setAtivo(true);
        usuario.setVoluntario(voluntario);

        pageable = PageRequest.of(0, 10);
    }

    @Test
    @DisplayName("Deve criar voluntário com sucesso")
    void criar_Sucesso() {
        
        when(voluntarioRepository.save(any(Voluntario.class))).thenReturn(voluntario);
        when(emailRepository.findFirstByVoluntarioUuid(voluntarioId)).thenReturn(Optional.of(email));
        doNothing().when(emailUtil).enviarEmail(anyString(), anyString(), anyString());

        
        VoluntarioDTO resultado = voluntarioService.criar(voluntarioDTO, emailDTOList, telefoneDTOList, formularioDTO);

        
        assertNotNull(resultado);
        assertEquals(voluntarioDTO.getNomeCompleto(), resultado.getNomeCompleto());

        verify(voluntarioRepository, times(1)).save(any(Voluntario.class));
        verify(emailRepository, times(1)).save(any(Email.class));
        verify(telefoneRepository, times(1)).save(any(Telefone.class));
        verify(formularioVoluntarioRepository, times(1)).save(any(FormularioVoluntario.class));
        verify(emailRepository, times(1)).findFirstByVoluntarioUuid(voluntarioId);
        verify(emailUtil, times(1)).enviarEmail(eq("joao@email.com"), eq("Status da Inscrição no Voluntariado"), contains("João"));
    }

    @Test
    @DisplayName("Deve criar voluntário com múltiplos emails e telefones")
    void criar_ComMultiplosEmailsETelefones() {
        
        EmailDTO email2 = new EmailDTO();
        email2.setEmail("joao2@email.com");
        email2.setUuidVoluntario(voluntarioId);

        TelefoneDTO telefone2 = new TelefoneDTO();
        telefone2.setDdd("11");
        telefone2.setTelefone("888888888");
        telefone2.setUuidVoluntario(voluntarioId);

        List<EmailDTO> multiplosEmails = Arrays.asList(emailDTOList.get(0), email2);
        List<TelefoneDTO> multiplosTelefones = Arrays.asList(telefoneDTOList.get(0), telefone2);

        when(voluntarioRepository.save(any(Voluntario.class))).thenReturn(voluntario);
        when(emailRepository.findFirstByVoluntarioUuid(voluntarioId)).thenReturn(Optional.of(email));
        doNothing().when(emailUtil).enviarEmail(anyString(), anyString(), anyString());

        
        VoluntarioDTO resultado = voluntarioService.criar(voluntarioDTO, multiplosEmails, multiplosTelefones, formularioDTO);

        
        assertNotNull(resultado);
        verify(emailRepository, times(2)).save(any(Email.class));
        verify(telefoneRepository, times(2)).save(any(Telefone.class));
    }

    @Test
    @DisplayName("Deve lançar UsuarioException quando email não encontrado para envio")
    void criar_EmailNaoEncontradoParaEnvio() {
        
        when(voluntarioRepository.save(any(Voluntario.class))).thenReturn(voluntario);
        when(emailRepository.findFirstByVoluntarioUuid(voluntarioId)).thenReturn(Optional.empty());

        
        UsuarioException exception = assertThrows(UsuarioException.class,
                () -> voluntarioService.criar(voluntarioDTO, emailDTOList, telefoneDTOList, formularioDTO));

        assertEquals("Nenhum e-mail encontrado para o voluntário.", exception.getMessage());
        verify(emailUtil, never()).enviarEmail(anyString(), anyString(), anyString());
    }

    @Test
    @DisplayName("Deve atualizar voluntário com sucesso")
    void atualizar_Sucesso() {
        
        when(voluntarioRepository.findById(voluntarioId)).thenReturn(Optional.of(voluntario));
        when(formularioVoluntarioRepository.findByVoluntario(voluntario)).thenReturn(Optional.of(formularioVoluntario));
        when(voluntarioRepository.save(any(Voluntario.class))).thenReturn(voluntario);

        
        assertDoesNotThrow(() -> voluntarioService.atualizar(voluntarioId, voluntarioDTO, emailDTOList, telefoneDTOList, formularioDTO));

        
        verify(voluntarioRepository, times(1)).findById(voluntarioId);
        verify(voluntarioRepository, times(1)).save(any(Voluntario.class));
        verify(emailRepository, times(1)).deleteByVoluntario(voluntario);
        verify(telefoneRepository, times(1)).deleteByVoluntario(voluntario);
        verify(emailRepository, times(1)).save(any(Email.class));
        verify(telefoneRepository, times(1)).save(any(Telefone.class));
        verify(formularioVoluntarioRepository, times(1)).save(formularioVoluntario);
    }

    @Test
    @DisplayName("Deve lançar RuntimeException quando voluntário não encontrado para atualização")
    void atualizar_VoluntarioNaoEncontrado() {
        
        when(voluntarioRepository.findById(voluntarioId)).thenReturn(Optional.empty());

        
        RuntimeException exception = assertThrows(RuntimeException.class,
                () -> voluntarioService.atualizar(voluntarioId, voluntarioDTO, emailDTOList, telefoneDTOList, formularioDTO));

        assertEquals("Voluntário não encontrado", exception.getMessage());
        verify(voluntarioRepository, never()).save(any());
    }

    @Test
    @DisplayName("Deve lançar RuntimeException quando formulário não encontrado para atualização")
    void atualizar_FormularioNaoEncontrado() {
        
        when(voluntarioRepository.findById(voluntarioId)).thenReturn(Optional.of(voluntario));
        when(formularioVoluntarioRepository.findByVoluntario(voluntario)).thenReturn(Optional.empty());

        
        RuntimeException exception = assertThrows(RuntimeException.class,
                () -> voluntarioService.atualizar(voluntarioId, voluntarioDTO, emailDTOList, telefoneDTOList, formularioDTO));

        assertEquals("Formulário não encontrado", exception.getMessage());
    }

    @Test
    @DisplayName("Deve remover voluntário com sucesso")
    void remover_Sucesso() {
        
        when(voluntarioRepository.findById(voluntarioId)).thenReturn(Optional.of(voluntario));
        when(voluntarioRepository.save(any(Voluntario.class))).thenReturn(voluntario);

        
        assertDoesNotThrow(() -> voluntarioService.remover(voluntarioId));

        
        assertFalse(voluntario.getAtivo());
        verify(voluntarioRepository, times(1)).findById(voluntarioId);
        verify(voluntarioRepository, times(1)).save(voluntario);
    }

    @Test
    @DisplayName("Deve lançar RuntimeException quando voluntário não encontrado para remoção")
    void remover_VoluntarioNaoEncontrado() {
        
        when(voluntarioRepository.findById(voluntarioId)).thenReturn(Optional.empty());

        
        RuntimeException exception = assertThrows(RuntimeException.class,
                () -> voluntarioService.remover(voluntarioId));

        assertEquals("Voluntário não encontrado com ID: " + voluntarioId, exception.getMessage());
        verify(voluntarioRepository, never()).save(any());
    }

    @Test
    @DisplayName("Deve buscar voluntário por ID com sucesso")
    void buscarPorId_Sucesso() {
        
        when(voluntarioRepository.findById(voluntarioId)).thenReturn(Optional.of(voluntario));
        when(emailRepository.findByVoluntario(voluntario)).thenReturn(Collections.singletonList(email));
        when(telefoneRepository.findByVoluntario(voluntario)).thenReturn(Collections.singletonList(telefone));
        when(formularioVoluntarioRepository.findByVoluntario(voluntario)).thenReturn(Optional.of(formularioVoluntario));

        
        VoluntarioResponseDTO resultado = voluntarioService.buscarPorId(voluntarioId);

        
        assertNotNull(resultado);
        assertNotNull(resultado.getEmailDTOList());
        assertNotNull(resultado.getTelefoneDTOList());
        assertNotNull(resultado.getFormularioDTO());
        assertEquals(1, resultado.getEmailDTOList().size());
        assertEquals(1, resultado.getTelefoneDTOList().size());

        verify(voluntarioRepository, times(1)).findById(voluntarioId);
        verify(emailRepository, times(1)).findByVoluntario(voluntario);
        verify(telefoneRepository, times(1)).findByVoluntario(voluntario);
        verify(formularioVoluntarioRepository, times(1)).findByVoluntario(voluntario);
    }

    @Test
    @DisplayName("Deve lançar RuntimeException quando voluntário não encontrado por ID")
    void buscarPorId_VoluntarioNaoEncontrado() {
        
        when(voluntarioRepository.findById(voluntarioId)).thenReturn(Optional.empty());

        
        RuntimeException exception = assertThrows(RuntimeException.class,
                () -> voluntarioService.buscarPorId(voluntarioId));

        assertEquals("Voluntário não encontrado", exception.getMessage());
    }

    @Test
    @DisplayName("Deve lançar RuntimeException quando formulário não encontrado por ID")
    void buscarPorId_FormularioNaoEncontrado() {
        
        when(voluntarioRepository.findById(voluntarioId)).thenReturn(Optional.of(voluntario));
        when(emailRepository.findByVoluntario(voluntario)).thenReturn(Collections.singletonList(email));
        when(telefoneRepository.findByVoluntario(voluntario)).thenReturn(Collections.singletonList(telefone));
        when(formularioVoluntarioRepository.findByVoluntario(voluntario)).thenReturn(Optional.empty());

        
        RuntimeException exception = assertThrows(RuntimeException.class,
                () -> voluntarioService.buscarPorId(voluntarioId));

        assertEquals("Formulário não encontrado", exception.getMessage());
    }

    @Test
    @DisplayName("Deve buscar todos voluntários com filtro ativo")
    void buscarTodos_ComFiltroAtivo() {
        
        Page<Voluntario> pageVoluntarios = new PageImpl<>(Collections.singletonList(voluntario));
        when(voluntarioRepository.findByAtivoAndAprovadoIsNotNull(true, pageable)).thenReturn(pageVoluntarios);
        when(usuarioRepository.findByVoluntario(voluntario)).thenReturn(usuario);
        when(emailRepository.findByVoluntario(voluntario)).thenReturn(Collections.singletonList(email));
        when(telefoneRepository.findByVoluntario(voluntario)).thenReturn(Collections.singletonList(telefone));
        when(formularioVoluntarioRepository.findByVoluntario(voluntario)).thenReturn(Optional.of(formularioVoluntario));

        
        Page<VoluntarioResponseDTO> resultado = voluntarioService.buscarTodos(true, pageable);

        
        assertNotNull(resultado);
        assertEquals(1, resultado.getContent().size());
        assertNotNull(resultado.getContent().get(0).getUsuarioDTO());
        assertNotNull(resultado.getContent().get(0).getFormularioDTO());

        verify(voluntarioRepository, times(1)).findByAtivoAndAprovadoIsNotNull(true, pageable);
        verify(voluntarioRepository, never()).findAllByAprovadoIsNotNull(any());
    }

    @Test
    @DisplayName("Deve buscar todos voluntários com filtro inativo")
    void buscarTodos_ComFiltroInativo() {
        
        voluntario.setAtivo(false);
        Page<Voluntario> pageVoluntarios = new PageImpl<>(List.of(voluntario));
        when(voluntarioRepository.findByAtivoAndAprovadoIsNotNull(false, pageable)).thenReturn(pageVoluntarios);
        when(usuarioRepository.findByVoluntario(voluntario)).thenReturn(usuario);
        when(emailRepository.findByVoluntario(voluntario)).thenReturn(Collections.singletonList(email));
        when(telefoneRepository.findByVoluntario(voluntario)).thenReturn(Collections.singletonList(telefone));
        when(formularioVoluntarioRepository.findByVoluntario(voluntario)).thenReturn(Optional.of(formularioVoluntario));

        
        Page<VoluntarioResponseDTO> resultado = voluntarioService.buscarTodos(false, pageable);

        
        assertNotNull(resultado);
        assertEquals(1, resultado.getContent().size());
        verify(voluntarioRepository, times(1)).findByAtivoAndAprovadoIsNotNull(false, pageable);
    }

    @Test
    @DisplayName("Deve buscar todos voluntários sem filtro")
    void buscarTodos_SemFiltro() {
        
        Page<Voluntario> pageVoluntarios = new PageImpl<>(Collections.singletonList(voluntario));
        when(voluntarioRepository.findAllByAprovadoIsNotNull(pageable)).thenReturn(pageVoluntarios);
        when(usuarioRepository.findByVoluntario(voluntario)).thenReturn(usuario);
        when(emailRepository.findByVoluntario(voluntario)).thenReturn(Collections.singletonList(email));
        when(telefoneRepository.findByVoluntario(voluntario)).thenReturn(Collections.singletonList(telefone));
        when(formularioVoluntarioRepository.findByVoluntario(voluntario)).thenReturn(Optional.of(formularioVoluntario));

        
        Page<VoluntarioResponseDTO> resultado = voluntarioService.buscarTodos(null, pageable);

        
        assertNotNull(resultado);
        assertEquals(1, resultado.getContent().size());
        verify(voluntarioRepository, times(1)).findAllByAprovadoIsNotNull(pageable);
        verify(voluntarioRepository, never()).findByAtivoAndAprovadoIsNotNull(anyBoolean(), any());
    }

    @Test
    @DisplayName("Deve buscar voluntários sem usuário associado")
    void buscarTodos_SemUsuarioAssociado() {
        
        Page<Voluntario> pageVoluntarios = new PageImpl<>(Collections.singletonList(voluntario));
        when(voluntarioRepository.findAllByAprovadoIsNotNull(pageable)).thenReturn(pageVoluntarios);
        when(usuarioRepository.findByVoluntario(voluntario)).thenReturn(null);
        when(emailRepository.findByVoluntario(voluntario)).thenReturn(Collections.singletonList(email));
        when(telefoneRepository.findByVoluntario(voluntario)).thenReturn(Collections.singletonList(telefone));
        when(formularioVoluntarioRepository.findByVoluntario(voluntario)).thenReturn(Optional.of(formularioVoluntario));

        
        Page<VoluntarioResponseDTO> resultado = voluntarioService.buscarTodos(null, pageable);

        
        assertNotNull(resultado);
        assertNull(resultado.getContent().get(0).getUsuarioDTO());
    }

    @Test
    @DisplayName("Deve buscar voluntários sem formulário")
    void buscarTodos_SemFormulario() {
        
        Page<Voluntario> pageVoluntarios = new PageImpl<>(Collections.singletonList(voluntario));
        when(voluntarioRepository.findAllByAprovadoIsNotNull(pageable)).thenReturn(pageVoluntarios);
        when(usuarioRepository.findByVoluntario(voluntario)).thenReturn(usuario);
        when(emailRepository.findByVoluntario(voluntario)).thenReturn(Collections.singletonList(email));
        when(telefoneRepository.findByVoluntario(voluntario)).thenReturn(Collections.singletonList(telefone));
        when(formularioVoluntarioRepository.findByVoluntario(voluntario)).thenReturn(Optional.empty());

        
        Page<VoluntarioResponseDTO> resultado = voluntarioService.buscarTodos(null, pageable);

        
        assertNotNull(resultado);
        assertNull(resultado.getContent().get(0).getFormularioDTO());
    }

    @Test
    @DisplayName("Deve atualizar status de aprovação para aprovado")
    void atualizarStatusAprovacao_Aprovado() {
        
        when(voluntarioRepository.findById(voluntarioId)).thenReturn(Optional.of(voluntario));
        when(voluntarioRepository.save(any(Voluntario.class))).thenReturn(voluntario);
        when(emailRepository.findFirstByVoluntarioUuid(voluntarioId)).thenReturn(Optional.of(email));
        doNothing().when(emailUtil).enviarEmail(anyString(), anyString(), anyString());

        
        assertDoesNotThrow(() -> voluntarioService.atualizarStatusAprovacao(voluntarioId, true));

        
        assertTrue(voluntario.getAprovado());
        assertTrue(voluntario.getAtivo());
        verify(voluntarioRepository, times(1)).findById(voluntarioId);
        verify(voluntarioRepository, times(1)).save(voluntario);
        verify(emailUtil, times(1)).enviarEmail(eq("joao@email.com"), eq("Parabéns, você foi aprovado!"), contains("João"));
    }

    @Test
    @DisplayName("Deve atualizar status de aprovação para reprovado")
    void atualizarStatusAprovacao_Reprovado() {
        
        when(voluntarioRepository.findById(voluntarioId)).thenReturn(Optional.of(voluntario));
        when(voluntarioRepository.save(any(Voluntario.class))).thenReturn(voluntario);
        when(emailRepository.findFirstByVoluntarioUuid(voluntarioId)).thenReturn(Optional.of(email));
        doNothing().when(emailUtil).enviarEmail(anyString(), anyString(), anyString());

        
        assertDoesNotThrow(() -> voluntarioService.atualizarStatusAprovacao(voluntarioId, false));

        
        assertFalse(voluntario.getAprovado());
        assertFalse(voluntario.getAtivo());
        verify(voluntarioRepository, times(1)).findById(voluntarioId);
        verify(voluntarioRepository, times(1)).save(voluntario);
        verify(emailUtil, times(1)).enviarEmail(eq("joao@email.com"), eq("Infelizmente, você não foi aprovado"), contains("João"));
    }

    @Test
    @DisplayName("Deve lançar RuntimeException quando voluntário não encontrado para atualizar status")
    void atualizarStatusAprovacao_VoluntarioNaoEncontrado() {
        
        when(voluntarioRepository.findById(voluntarioId)).thenReturn(Optional.empty());

        
        RuntimeException exception = assertThrows(RuntimeException.class,
                () -> voluntarioService.atualizarStatusAprovacao(voluntarioId, true));

        assertEquals("Voluntário não encontrado com ID: " + voluntarioId, exception.getMessage());
        verify(emailUtil, never()).enviarEmail(anyString(), anyString(), anyString());
    }

    @Test
    @DisplayName("Deve lançar RuntimeException quando email não encontrado para aprovação")
    void atualizarStatusAprovacao_EmailNaoEncontrado() {
        
        when(voluntarioRepository.findById(voluntarioId)).thenReturn(Optional.of(voluntario));
        when(voluntarioRepository.save(any(Voluntario.class))).thenReturn(voluntario);
        when(emailRepository.findFirstByVoluntarioUuid(voluntarioId)).thenReturn(Optional.empty());

        
        RuntimeException exception = assertThrows(RuntimeException.class,
                () -> voluntarioService.atualizarStatusAprovacao(voluntarioId, true));

        assertEquals("Nenhum e-mail encontrado para o voluntário.", exception.getMessage());
        verify(emailUtil, never()).enviarEmail(anyString(), anyString(), anyString());
    }

    @Test
    @DisplayName("Deve buscar novos voluntários com sucesso")
    void buscarNovos_Sucesso() {
        
        voluntario.setAprovado(null);
        Page<Voluntario> pageVoluntarios = new PageImpl<>(List.of(voluntario));
        when(voluntarioRepository.findByAprovadoIsNull(pageable)).thenReturn(pageVoluntarios);
        when(emailRepository.findByVoluntario(voluntario)).thenReturn(Collections.singletonList(email));
        when(telefoneRepository.findByVoluntario(voluntario)).thenReturn(Collections.singletonList(telefone));
        when(formularioVoluntarioRepository.findByVoluntario(voluntario)).thenReturn(Optional.of(formularioVoluntario));

        
        Page<VoluntarioResponseDTO> resultado = voluntarioService.buscarNovos(pageable);

        
        assertNotNull(resultado);
        assertEquals(1, resultado.getContent().size());
        assertNotNull(resultado.getContent().get(0).getFormularioDTO());
        verify(voluntarioRepository, times(1)).findByAprovadoIsNull(pageable);
    }

    @Test
    @DisplayName("Deve buscar novos voluntários sem formulário")
    void buscarNovos_SemFormulario() {
        
        voluntario.setAprovado(null);
        Page<Voluntario> pageVoluntarios = new PageImpl<>(List.of(voluntario));
        when(voluntarioRepository.findByAprovadoIsNull(pageable)).thenReturn(pageVoluntarios);
        when(emailRepository.findByVoluntario(voluntario)).thenReturn(Collections.singletonList(email));
        when(telefoneRepository.findByVoluntario(voluntario)).thenReturn(Collections.singletonList(telefone));
        when(formularioVoluntarioRepository.findByVoluntario(voluntario)).thenReturn(Optional.empty());

        
        Page<VoluntarioResponseDTO> resultado = voluntarioService.buscarNovos(pageable);

        
        assertNotNull(resultado);
        assertNull(resultado.getContent().get(0).getFormularioDTO());
    }

    @Test
    @DisplayName("Deve retornar página vazia quando não há novos voluntários")
    void buscarNovos_PaginaVazia() {
        
        Page<Voluntario> paginaVazia = new PageImpl<>(Collections.emptyList());
        when(voluntarioRepository.findByAprovadoIsNull(pageable)).thenReturn(paginaVazia);

        
        Page<VoluntarioResponseDTO> resultado = voluntarioService.buscarNovos(pageable);

        
        assertNotNull(resultado);
        assertTrue(resultado.getContent().isEmpty());
    }

    @Test
    @DisplayName("Deve enviar email de status com nome de uma palavra")
    void criar_ComNomeUmaPalavra() {
        
        voluntario.setNomeCompleto("João");
        voluntarioDTO.setNomeCompleto("João");

        when(voluntarioRepository.save(any(Voluntario.class))).thenReturn(voluntario);
        when(emailRepository.findFirstByVoluntarioUuid(voluntarioId)).thenReturn(Optional.of(email));
        doNothing().when(emailUtil).enviarEmail(anyString(), anyString(), anyString());

        
        VoluntarioDTO resultado = voluntarioService.criar(voluntarioDTO, emailDTOList, telefoneDTOList, formularioDTO);

        
        assertNotNull(resultado);
        verify(emailUtil, times(1)).enviarEmail(eq("joao@email.com"), eq("Status da Inscrição no Voluntariado"), contains("João"));
    }

    @Test
    @DisplayName("Deve enviar email de aprovação com nome de uma palavra")
    void atualizarStatusAprovacao_ComNomeUmaPalavra() {
        
        voluntario.setNomeCompleto("Maria");
        when(voluntarioRepository.findById(voluntarioId)).thenReturn(Optional.of(voluntario));
        when(voluntarioRepository.save(any(Voluntario.class))).thenReturn(voluntario);
        when(emailRepository.findFirstByVoluntarioUuid(voluntarioId)).thenReturn(Optional.of(email));
        doNothing().when(emailUtil).enviarEmail(anyString(), anyString(), anyString());

        
        assertDoesNotThrow(() -> voluntarioService.atualizarStatusAprovacao(voluntarioId, true));

        
        verify(emailUtil, times(1)).enviarEmail(eq("joao@email.com"), eq("Parabéns, você foi aprovado!"), contains("Maria"));
    }
}
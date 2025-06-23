package br.com.sol_do_amanhecer.service.impl;

import br.com.sol_do_amanhecer.model.dto.*;
import br.com.sol_do_amanhecer.model.entity.*;
import br.com.sol_do_amanhecer.repository.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.*;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@DisplayName("VoluntarioServiceImpl - Testes Unitários")
class VoluntarioServiceImplTest {

    @Mock
    private VoluntarioRepository voluntarioRepository;

    @Mock
    private EmailRepository emailRepository;

    @Mock
    private TelefoneRepository telefoneRepository;

    @Mock
    private FormularioVoluntarioRepository formularioVoluntarioRepository;

    @InjectMocks
    private VoluntarioServiceImpl voluntarioService;

    private VoluntarioDTO voluntarioDTO;
    private List<EmailDTO> emailDTOList;
    private List<TelefoneDTO> telefoneDTOList;
    private FormularioVoluntarioDTO formularioDTO;
    private Voluntario voluntario;
    private UUID voluntarioId;

    @BeforeEach
    void setUp() {
        voluntarioId = UUID.randomUUID();

        EnderecoDTO enderecoDTO = EnderecoDTO.builder()
                .logradouro("Rua Teste")
                .numero("123")
                .bairro("Centro")
                .cidade("São Paulo")
                .estado("SP")
                .cep("12345-678")
                .build();

        voluntarioDTO = VoluntarioDTO.builder()
                .uuid(voluntarioId)
                .nomeCompleto("João Silva")
                .dataNascimento(LocalDate.of(1990, 1, 1))
                .enderecoDTO(enderecoDTO)
                .ativo(true)
                .build();

        emailDTOList = Arrays.asList(
                EmailDTO.builder()
                        .uuidVoluntario(voluntarioId)
                        .email("joao@email.com")
                        .build(),
                EmailDTO.builder()
                        .uuidVoluntario(voluntarioId)
                        .email("joao.silva@email.com")
                        .build()
        );

        telefoneDTOList = Arrays.asList(
                TelefoneDTO.builder()
                        .uuidVoluntario(voluntarioId)
                        .ddd("11")
                        .telefone("999999999")
                        .build(),
                TelefoneDTO.builder()
                        .uuidVoluntario(voluntarioId)
                        .ddd("11")
                        .telefone("888888888")
                        .build()
        );

        formularioDTO = FormularioVoluntarioDTO.builder()
                .uuidVoluntario(voluntarioId)
                .comoConheceu("Internet")
                .motivoVoluntariado("Ajudar pessoas")
                .cienteTrabalhoVoluntario(true)
                .dedicacaoVoluntariado("10 horas semanais")
                .disponibilidadeSemana("Fins de semana")
                .compromissoDivulgar(true)
                .compromissoAcao(true)
                .desejaCamisa(true)
                .sobreMim("Sou uma pessoa dedicada")
                .dataResposta(LocalDateTime.now())
                .build();

        Endereco endereco = new Endereco();
        endereco.setLogradouro("Rua Teste");
        endereco.setNumero("123");

        voluntario = new Voluntario();
        voluntario.setUuid(voluntarioId);
        voluntario.setNomeCompleto("João Silva");
        voluntario.setDataNascimento(LocalDate.of(1990, 1, 1));
        voluntario.setEndereco(endereco);
        voluntario.setAtivo(true);
    }

    @Nested
    @DisplayName("Criar Voluntário")
    class CriarVoluntario {

        @Test
        @DisplayName("Deve criar voluntário com sucesso")
        void deveCriarVoluntarioComSucesso() {
            when(voluntarioRepository.save(any(Voluntario.class))).thenReturn(voluntario);

            VoluntarioDTO resultado = voluntarioService.criar(
                    voluntarioDTO, emailDTOList, telefoneDTOList, formularioDTO
            );

            assertThat(resultado).isNotNull();
            verify(voluntarioRepository, times(1)).save(any(Voluntario.class));
            verify(emailRepository, times(2)).save(any(Email.class));
            verify(telefoneRepository, times(2)).save(any(Telefone.class));
            verify(formularioVoluntarioRepository, times(1)).save(any(FormularioVoluntario.class));
        }

        @Test
        @DisplayName("Deve criar voluntário com lista vazia de emails")
        void deveCriarVoluntarioComListaVaziaDeEmails() {
            when(voluntarioRepository.save(any(Voluntario.class))).thenReturn(voluntario);
            List<EmailDTO> emailsVazios = new ArrayList<>();

            VoluntarioDTO resultado = voluntarioService.criar(
                    voluntarioDTO, emailsVazios, telefoneDTOList, formularioDTO
            );

            assertThat(resultado).isNotNull();
            verify(emailRepository, never()).save(any(Email.class));
        }
    }

    @Nested
    @DisplayName("Atualizar Voluntário")
    class AtualizarVoluntario {

        @Test
        @DisplayName("Deve atualizar voluntário com sucesso")
        void deveAtualizarVoluntarioComSucesso() {
            when(voluntarioRepository.findById(voluntarioId)).thenReturn(Optional.of(voluntario));
            when(voluntarioRepository.save(any(Voluntario.class))).thenReturn(voluntario);

            FormularioVoluntario formularioExistente = new FormularioVoluntario();
            when(formularioVoluntarioRepository.findByVoluntario(any(Voluntario.class)))
                    .thenReturn(Optional.of(formularioExistente));

            assertThatCode(() ->
                    voluntarioService.atualizar(voluntarioId, voluntarioDTO, emailDTOList,
                            telefoneDTOList, formularioDTO)
            ).doesNotThrowAnyException();

            verify(voluntarioRepository).findById(voluntarioId);
            verify(voluntarioRepository).save(any(Voluntario.class));
            verify(emailRepository).deleteByVoluntario(any(Voluntario.class));
            verify(telefoneRepository).deleteByVoluntario(any(Voluntario.class));
            verify(formularioVoluntarioRepository).save(any(FormularioVoluntario.class));
        }

        @Test
        @DisplayName("Deve lançar exceção quando voluntário não existe")
        void deveLancarExcecaoQuandoVoluntarioNaoExiste() {
            when(voluntarioRepository.findById(voluntarioId)).thenReturn(Optional.empty());

            assertThatThrownBy(() ->
                    voluntarioService.atualizar(voluntarioId, voluntarioDTO, emailDTOList,
                            telefoneDTOList, formularioDTO)
            )
                    .isInstanceOf(RuntimeException.class)
                    .hasMessage("Voluntário não encontrado");
        }

        @Test
        @DisplayName("Deve lançar exceção quando formulário não existe")
        void deveLancarExcecaoQuandoFormularioNaoExiste() {
            when(voluntarioRepository.findById(voluntarioId)).thenReturn(Optional.of(voluntario));
            when(formularioVoluntarioRepository.findByVoluntario(any(Voluntario.class)))
                    .thenReturn(Optional.empty());

            assertThatThrownBy(() ->
                    voluntarioService.atualizar(voluntarioId, voluntarioDTO, emailDTOList,
                            telefoneDTOList, formularioDTO)
            )
                    .isInstanceOf(RuntimeException.class)
                    .hasMessage("Formulário não encontrado");
        }
    }

    @Nested
    @DisplayName("Remover Voluntário")
    class RemoverVoluntario {

        @Test
        @DisplayName("Deve remover voluntário com sucesso")
        void deveRemoverVoluntarioComSucesso() {
            when(voluntarioRepository.existsById(voluntarioId)).thenReturn(true);

            assertThatCode(() -> voluntarioService.remover(voluntarioId))
                    .doesNotThrowAnyException();

            verify(voluntarioRepository).existsById(voluntarioId);
            verify(voluntarioRepository).deleteById(voluntarioId);
        }

        @Test
        @DisplayName("Deve lançar exceção quando voluntário não existe")
        void deveLancarExcecaoQuandoVoluntarioNaoExiste() {
            when(voluntarioRepository.existsById(voluntarioId)).thenReturn(false);

            assertThatThrownBy(() -> voluntarioService.remover(voluntarioId))
                    .isInstanceOf(RuntimeException.class)
                    .hasMessage("Voluntário não encontrado");

            verify(voluntarioRepository, never()).deleteById(any(UUID.class));
        }
    }

    @Nested
    @DisplayName("Buscar Voluntário Por ID")
    class BuscarVoluntarioPorId {

        @Test
        @DisplayName("Deve buscar voluntário por ID com sucesso")
        void deveBuscarVoluntarioPorIdComSucesso() {
            when(voluntarioRepository.findById(voluntarioId)).thenReturn(Optional.of(voluntario));

            List<Email> emails = Arrays.asList(new Email(), new Email());
            when(emailRepository.findByVoluntario(voluntario)).thenReturn(emails);

            List<Telefone> telefones = Arrays.asList(new Telefone(), new Telefone());
            when(telefoneRepository.findByVoluntario(voluntario)).thenReturn(telefones);

            FormularioVoluntario formulario = new FormularioVoluntario();
            when(formularioVoluntarioRepository.findByVoluntario(voluntario))
                    .thenReturn(Optional.of(formulario));

            VoluntarioResponseDTO resultado = voluntarioService.buscarPorId(voluntarioId);

            assertThat(resultado).isNotNull();
            verify(voluntarioRepository).findById(voluntarioId);
            verify(emailRepository).findByVoluntario(voluntario);
            verify(telefoneRepository).findByVoluntario(voluntario);
            verify(formularioVoluntarioRepository).findByVoluntario(voluntario);
        }

        @Test
        @DisplayName("Deve lançar exceção quando voluntário não existe")
        void deveLancarExcecaoQuandoVoluntarioNaoExiste() {
            when(voluntarioRepository.findById(voluntarioId)).thenReturn(Optional.empty());

            assertThatThrownBy(() -> voluntarioService.buscarPorId(voluntarioId))
                    .isInstanceOf(RuntimeException.class)
                    .hasMessage("Voluntário não encontrado");
        }

        @Test
        @DisplayName("Deve lançar exceção quando formulário não existe")
        void deveLancarExcecaoQuandoFormularioNaoExiste() {
            when(voluntarioRepository.findById(voluntarioId)).thenReturn(Optional.of(voluntario));
            when(emailRepository.findByVoluntario(voluntario)).thenReturn(new ArrayList<>());
            when(telefoneRepository.findByVoluntario(voluntario)).thenReturn(new ArrayList<>());
            when(formularioVoluntarioRepository.findByVoluntario(voluntario))
                    .thenReturn(Optional.empty());

            assertThatThrownBy(() -> voluntarioService.buscarPorId(voluntarioId))
                    .isInstanceOf(RuntimeException.class)
                    .hasMessage("Formulário não encontrado");
        }
    }

    @Nested
    @DisplayName("Buscar Todos Voluntários")
    class BuscarTodosVoluntarios {

        @Test
        @DisplayName("Deve buscar todos voluntários com sucesso")
        void deveBuscarTodosVoluntariosComSucesso() {
            List<Voluntario> voluntarios = Arrays.asList(voluntario, new Voluntario());
            when(voluntarioRepository.findAll()).thenReturn(voluntarios);

            when(emailRepository.findByVoluntario(any(Voluntario.class)))
                    .thenReturn(new ArrayList<>());
            when(telefoneRepository.findByVoluntario(any(Voluntario.class)))
                    .thenReturn(new ArrayList<>());
            when(formularioVoluntarioRepository.findByVoluntario(any(Voluntario.class)))
                    .thenReturn(Optional.of(new FormularioVoluntario()));

            List<VoluntarioResponseDTO> resultado = voluntarioService.buscarTodos();

            assertThat(resultado).hasSize(2);
            verify(voluntarioRepository).findAll();
        }

        @Test
        @DisplayName("Deve retornar lista vazia quando não há voluntários")
        void deveRetornarListaVaziaQuandoNaoHaVoluntarios() {
            when(voluntarioRepository.findAll()).thenReturn(new ArrayList<>());

            List<VoluntarioResponseDTO> resultado = voluntarioService.buscarTodos();

            assertThat(resultado).isEmpty();
        }

        @Test
        @DisplayName("Deve buscar voluntários mesmo sem formulário")
        void deveBuscarVoluntariosMesmoSemFormulario() {
            List<Voluntario> voluntarios = Arrays.asList(voluntario);
            when(voluntarioRepository.findAll()).thenReturn(voluntarios);

            when(emailRepository.findByVoluntario(any(Voluntario.class)))
                    .thenReturn(new ArrayList<>());
            when(telefoneRepository.findByVoluntario(any(Voluntario.class)))
                    .thenReturn(new ArrayList<>());
            when(formularioVoluntarioRepository.findByVoluntario(any(Voluntario.class)))
                    .thenReturn(Optional.empty());

            List<VoluntarioResponseDTO> resultado = voluntarioService.buscarTodos();

            assertThat(resultado).hasSize(1);
            assertThat(resultado.get(0).getFormularioDTO()).isNull();
        }
    }
}
package br.com.sol_do_amanhecer.service.impl;

import br.com.sol_do_amanhecer.exception.UsuarioException;
import br.com.sol_do_amanhecer.model.dto.*;
import br.com.sol_do_amanhecer.model.entity.*;
import br.com.sol_do_amanhecer.model.mapper.*;
import br.com.sol_do_amanhecer.repository.*;
import br.com.sol_do_amanhecer.service.VoluntarioService;
import br.com.sol_do_amanhecer.util.EmailUtil;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class VoluntarioServiceImpl implements VoluntarioService {

    private final Logger LOGGER = LoggerFactory.getLogger(VoluntarioServiceImpl.class);
    private final VoluntarioRepository voluntarioRepository;
    private final EmailRepository emailRepository;
    private final TelefoneRepository telefoneRepository;
    private final FormularioVoluntarioRepository formularioVoluntarioRepository;
    private final UsuarioRepository usuarioRepository;
    private final EmailUtil emailUtil;

    private final VoluntarioMapper voluntarioMapper = VoluntarioMapper.INSTANCE;
    private final EmailMapper emailMapper = EmailMapper.INSTANCE;
    private final EnderecoMapper enderecoMapper = EnderecoMapper.INSTANCE;
    private final TelefoneMapper telefoneMapper = TelefoneMapper.INSTANCE;
    private final FormularioVoluntarioMapper formularioMapper = FormularioVoluntarioMapper.INSTANCE;
    private final UsuarioMapper usuarioMapper = UsuarioMapper.INSTANCE;

    @Override
    @Transactional
    public VoluntarioDTO criar(VoluntarioDTO voluntarioDTO, List<EmailDTO> emailDTOList,
                               List<TelefoneDTO> telefoneDTOList, FormularioVoluntarioDTO formularioDTO) {
        LOGGER.info("Criando um voluntário completo");

        Voluntario voluntario = voluntarioMapper.dtoParaEntity(voluntarioDTO);
        voluntario.setEndereco(voluntario.getEndereco());
        voluntario.setAtivo(false);
        voluntario.setAprovado(null);

        Voluntario voluntarioSalvo = voluntarioRepository.save(voluntario);

        emailDTOList.forEach(emailDTO -> {
            Email email = emailMapper.dtoParaEntity(emailDTO);
            email.setVoluntario(voluntarioSalvo);
            emailRepository.save(email);
        });

        telefoneDTOList.forEach(telefoneDTO -> {
            Telefone telefone = telefoneMapper.dtoParaEntity(telefoneDTO);
            telefone.setVoluntario(voluntarioSalvo);
            telefoneRepository.save(telefone);
        });

        FormularioVoluntario formularioVoluntario = formularioMapper.dtoParaEntity(formularioDTO);
        formularioVoluntario.setVoluntario(voluntarioSalvo);
        formularioVoluntarioRepository.save(formularioVoluntario);

        enviarEmailStatusVoluntario(voluntarioSalvo);

        return voluntarioMapper.entityParaDto(voluntarioSalvo);
    }

    @Override
    @Transactional
    public void atualizar(UUID id, VoluntarioDTO voluntarioDTO, List<EmailDTO> emailDTOs, List<TelefoneDTO> telefoneDTOs, FormularioVoluntarioDTO formularioDTO) {
        LOGGER.info("Atualizando voluntário com ID: {}", id);

        Voluntario voluntarioExistente = voluntarioRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Voluntário não encontrado"));

        voluntarioMapper.dtoParaEntity(voluntarioDTO);

        voluntarioExistente.setNomeCompleto(voluntarioDTO.getNomeCompleto());
        voluntarioExistente.setDataNascimento(voluntarioDTO.getDataNascimento());
        voluntarioExistente.setAtivo(voluntarioDTO.getAtivo());

        Endereco endereco = enderecoMapper.dtoParaEntity(voluntarioDTO.getEnderecoDTO());
        voluntarioExistente.setEndereco(endereco);

        atualizarEmails(voluntarioExistente, emailDTOs);
        atualizarTelefones(voluntarioExistente, telefoneDTOs);
        atualizarFormulario(voluntarioExistente, formularioDTO);

        voluntarioRepository.save(voluntarioExistente);
    }

    @Override
    @Transactional
    public void remover(UUID id) {
        LOGGER.info("Desativando voluntário com ID: {}", id);

        Voluntario voluntario = voluntarioRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Voluntário não encontrado com ID: " + id));

        voluntario.setAtivo(false);

        voluntarioRepository.save(voluntario);
    }

    @Override
    public VoluntarioResponseDTO buscarPorId(UUID id) {
        LOGGER.info("Buscando voluntário com ID: {}", id);

        Voluntario voluntario = voluntarioRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Voluntário não encontrado"));

        List<Email> emails = emailRepository.findByVoluntario(voluntario);
        List<Telefone> telefones = telefoneRepository.findByVoluntario(voluntario);
        FormularioVoluntario formulario = formularioVoluntarioRepository.findByVoluntario(voluntario)
                .orElseThrow(() -> new RuntimeException("Formulário não encontrado"));

        VoluntarioResponseDTO voluntarioResponseDTO = voluntarioMapper.entityParaResponseDto(voluntario);

        voluntarioResponseDTO.setEmailDTOList(emails.stream().map(emailMapper::entityParaDto).collect(Collectors.toList()));
        voluntarioResponseDTO.setTelefoneDTOList(telefones.stream().map(telefoneMapper::entityParaDto).collect(Collectors.toList()));
        voluntarioResponseDTO.setFormularioDTO(formularioMapper.entityParaDto(formulario));

        return voluntarioResponseDTO;
    }

    @Override
    public Page<VoluntarioResponseDTO> buscarTodos(Boolean ativo, Pageable pageable) {
        Page<Voluntario> voluntarios;

        if (ativo != null) {
            LOGGER.info("Buscando voluntários filtrados por ativo: {}", ativo);
            voluntarios = voluntarioRepository.findByAtivoAndAprovadoIsNotNull(ativo, pageable);
        } else {
            LOGGER.info("Buscando todos os voluntários sem filtro");
            voluntarios = voluntarioRepository.findAllByAprovadoIsNotNull(pageable);
        }

        return voluntarios.map(voluntario -> {
            VoluntarioResponseDTO dto = voluntarioMapper.entityParaResponseDto(voluntario);

            Usuario usuario = usuarioRepository.findByVoluntario(voluntario);

            UsuarioStatusIdDTO usuarioDto = usuarioMapper.entityParaDtoStatus(usuario);

            List<Email> emails = emailRepository.findByVoluntario(voluntario);
            List<Telefone> telefones = telefoneRepository.findByVoluntario(voluntario);

            dto.setUsuarioDTO(usuarioDto);
            dto.setEmailDTOList(emails.stream().map(emailMapper::entityParaDto).collect(Collectors.toList()));
            dto.setTelefoneDTOList(telefones.stream().map(telefoneMapper::entityParaDto).collect(Collectors.toList()));

            FormularioVoluntario formulario = formularioVoluntarioRepository.findByVoluntario(voluntario).orElse(null);
            if (formulario != null) {
                dto.setFormularioDTO(formularioMapper.entityParaDto(formulario));
            }

            return dto;
        });
    }

    private void atualizarEmails(Voluntario voluntario, List<EmailDTO> emailDTOs) {
        emailRepository.deleteByVoluntario(voluntario);

        emailDTOs.forEach(emailDTO -> {
            Email email = emailMapper.dtoParaEntity(emailDTO);
            email.setVoluntario(voluntario);
            emailRepository.save(email);
        });
    }

    private void atualizarTelefones(Voluntario voluntario, List<TelefoneDTO> telefoneDTOs) {
        telefoneRepository.deleteByVoluntario(voluntario);

        telefoneDTOs.forEach(telefoneDTO -> {
            Telefone telefone = telefoneMapper.dtoParaEntity(telefoneDTO);
            telefone.setVoluntario(voluntario);
            telefoneRepository.save(telefone);
        });
    }

    private void atualizarFormulario(Voluntario voluntario, FormularioVoluntarioDTO formularioDTO) {
        FormularioVoluntario formularioAtual = formularioVoluntarioRepository.findByVoluntario(voluntario)
                .orElseThrow(() -> new RuntimeException("Formulário não encontrado"));

        formularioAtual.setComoConheceu(formularioDTO.getComoConheceu());
        formularioAtual.setMotivoVoluntariado(formularioDTO.getMotivoVoluntariado());
        formularioAtual.setCienteTrabalhoVoluntario(formularioDTO.getCienteTrabalhoVoluntario());
        formularioAtual.setDedicacaoVoluntariado(formularioDTO.getDedicacaoVoluntariado());
        formularioAtual.setDisponibilidadeSemana(formularioDTO.getDisponibilidadeSemana());
        formularioAtual.setCompromissoDivulgar(formularioDTO.getCompromissoDivulgar());
        formularioAtual.setCompromissoAcao(formularioDTO.getCompromissoAcao());
        formularioAtual.setDesejaCamisa(formularioDTO.getDesejaCamisa());
        formularioAtual.setSobreMim(formularioDTO.getSobreMim());
        formularioAtual.setDataResposta(formularioDTO.getDataResposta());

        formularioVoluntarioRepository.save(formularioAtual);
    }

    @Override
    @Transactional
    public void atualizarStatusAprovacao(UUID id, Boolean aprovado) {
        LOGGER.info("Atualizando status de aprovação do voluntário com ID: {} para {}", id, aprovado);

        Voluntario voluntario = voluntarioRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Voluntário não encontrado com ID: " + id));

        voluntario.setAprovado(aprovado);

        if (Boolean.TRUE.equals(aprovado)) {
            voluntario.setAtivo(true);
        } else {
            voluntario.setAtivo(false);
        }

        voluntarioRepository.save(voluntario);

        enviarEmailStatusAprovacao(voluntario, aprovado);
    }

    @Override
    public Page<VoluntarioResponseDTO> buscarNovos(Pageable pageable) {
        LOGGER.info("Buscando todos os novos voluntários com status de aprovação nulo");

        Page<Voluntario> novosVoluntarios = voluntarioRepository.findByAprovadoIsNull(pageable);

        return novosVoluntarios.map(voluntario -> {
            VoluntarioResponseDTO dto = voluntarioMapper.entityParaResponseDto(voluntario);

            List<Email> emails = emailRepository.findByVoluntario(voluntario);
            List<Telefone> telefones = telefoneRepository.findByVoluntario(voluntario);
            FormularioVoluntario formulario = formularioVoluntarioRepository.findByVoluntario(voluntario).orElse(null);

            dto.setEmailDTOList(emails.stream().map(emailMapper::entityParaDto).collect(Collectors.toList()));
            dto.setTelefoneDTOList(telefones.stream().map(telefoneMapper::entityParaDto).collect(Collectors.toList()));

            if (formulario != null) {
                dto.setFormularioDTO(formularioMapper.entityParaDto(formulario));
            }

            return dto;
        });
    }

    private void enviarEmailStatusVoluntario(Voluntario voluntario) {
        LOGGER.info("Iniciando envio de e-mail sobre o status do voluntário: {}", voluntario.getUuid());

        Email email = emailRepository.findFirstByVoluntarioUuid(voluntario.getUuid())
                .orElseThrow(() -> new UsuarioException("Nenhum e-mail encontrado para o voluntário."));
        String primeiroNome = voluntario.getNomeCompleto().split(" ")[0];
        String assunto = "Status da Inscrição no Voluntariado";
        String mensagem = String.format(
                "Olá, %s!\n\n" +
                        "Obrigado por se inscrever como voluntário no Sol do Amanhecer. Estamos revisando sua inscrição.\n\n" +
                        "Fique atento ao seu e-mail para atualizações futuras sobre a sua aprovação ou reprovação!\n\n" +
                        "Atenciosamente,\nEquipe Sol do Amanhecer.",
                primeiroNome
        );

        emailUtil.enviarEmail(email.getEmail(), assunto, mensagem);

        LOGGER.info("E-mail sobre o status do voluntário enviado com sucesso para: {}", email.getEmail());
    }

    private void enviarEmailStatusAprovacao(Voluntario voluntario, Boolean aprovado) {
        LOGGER.info("Enviando e-mail sobre status de aprovação para o voluntário: {}", voluntario.getUuid());

        Email email = emailRepository.findFirstByVoluntarioUuid(voluntario.getUuid())
                .orElseThrow(() -> new RuntimeException("Nenhum e-mail encontrado para o voluntário."));

        String assunto;
        String mensagem;
        String primeiroNome = voluntario.getNomeCompleto().split(" ")[0];

        if (Boolean.TRUE.equals(aprovado)) {
            assunto = "Parabéns, você foi aprovado!";
            mensagem = String.format(
                    "Olá, %s!\n\n" +
                            "Temos boas notícias! Após analisar o seu cadastro, você foi aprovado(a) como voluntário(a) no Sol do Amanhecer.\n\n" +
                            "Agora, você já está ativo(a) para contribuir com nossas ações! Entraremos em contato com você via WhatsApp.\n\n" +
                            "Estamos muito felizes por ter você conosco!\n\n" +
                            "Atenciosamente,\nEquipe Sol do Amanhecer.",
                    primeiroNome
            );
        } else {
            assunto = "Infelizmente, você não foi aprovado";
            mensagem = String.format(
                    "Olá, %s!\n\n" +
                            "Após analisarmos o seu perfil, infelizmente, constatamos que você não atende aos critérios para se tornar voluntário(a) no Sol do Amanhecer neste momento.\n\n" +
                            "Queremos agradecer de coração pelo seu interesse em colaborar. Não desista de acompanhar nossas ações, e fique à vontade para se candidatar novamente no futuro!\n\n" +
                            "Se tiver dúvidas ou precisar de mais informações, entre em contato conosco.\n\n" +
                            "Atenciosamente,\nEquipe Sol do Amanhecer.",
                    primeiroNome
            );
        }

        emailUtil.enviarEmail(email.getEmail(), assunto, mensagem);

        LOGGER.info("E-mail enviado sobre o status de aprovação para: {}", email.getEmail());
    }
}
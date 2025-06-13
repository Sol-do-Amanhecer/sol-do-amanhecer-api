package br.com.sol_do_amanhecer.service.impl;

import br.com.sol_do_amanhecer.model.dto.*;
import br.com.sol_do_amanhecer.model.entity.*;
import br.com.sol_do_amanhecer.model.mapper.*;
import br.com.sol_do_amanhecer.repository.EmailRepository;
import br.com.sol_do_amanhecer.repository.FormularioVoluntarioRepository;
import br.com.sol_do_amanhecer.repository.TelefoneRepository;
import br.com.sol_do_amanhecer.repository.VoluntarioRepository;
import br.com.sol_do_amanhecer.service.VoluntarioService;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
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

    private final VoluntarioMapper voluntarioMapper = VoluntarioMapper.INSTANCE;
    private final EmailMapper emailMapper = EmailMapper.INSTANCE;
    private final EnderecoMapper enderecoMapper = EnderecoMapper.INSTANCE;
    private final TelefoneMapper telefoneMapper = TelefoneMapper.INSTANCE;
    private final FormularioVoluntarioMapper formularioMapper = FormularioVoluntarioMapper.INSTANCE;

    @Override
    @Transactional
    public VoluntarioDTO criar(VoluntarioDTO voluntarioDTO, List<EmailDTO> emailDTOList,
                               List<TelefoneDTO> telefoneDTOList, FormularioVoluntarioDTO formularioDTO) {
        LOGGER.info("Criando um voluntário completo");

        Voluntario voluntario = voluntarioMapper.dtoParaEntity(voluntarioDTO);
        voluntario.setEndereco(voluntario.getEndereco());

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
        LOGGER.info("Removendo voluntário com ID: {}", id);

        if (!voluntarioRepository.existsById(id)) {
            throw new RuntimeException("Voluntário não encontrado");
        }

        voluntarioRepository.deleteById(id);
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
    public List<VoluntarioResponseDTO> buscarTodos() {
        LOGGER.info("Buscando todos os voluntários");

        List<Voluntario> voluntarios = voluntarioRepository.findAll();

        return voluntarios.stream().map(voluntario -> {
            VoluntarioResponseDTO voluntarioResponseDTO = voluntarioMapper.entityParaResponseDto(voluntario);

            List<Email> emails = emailRepository.findByVoluntario(voluntario);
            List<Telefone> telefones = telefoneRepository.findByVoluntario(voluntario);
            FormularioVoluntario formulario = formularioVoluntarioRepository.findByVoluntario(voluntario).orElse(null);

            voluntarioResponseDTO.setEmailDTOList(emails.stream().map(emailMapper::entityParaDto).collect(Collectors.toList()));
            voluntarioResponseDTO.setTelefoneDTOList(telefones.stream().map(telefoneMapper::entityParaDto).collect(Collectors.toList()));
            if (formulario != null) {
                voluntarioResponseDTO.setFormularioDTO(formularioMapper.entityParaDto(formulario));
            }

            return voluntarioResponseDTO;
        }).collect(Collectors.toList());
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
}
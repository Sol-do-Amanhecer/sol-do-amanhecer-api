package br.com.sol_do_amanhecer.service.impl;

import br.com.sol_do_amanhecer.model.dto.AcaoDTO;
import br.com.sol_do_amanhecer.model.dto.AcaoResponseDTO;
import br.com.sol_do_amanhecer.model.dto.ImagemAcaoDTO;
import br.com.sol_do_amanhecer.model.entity.Acao;
import br.com.sol_do_amanhecer.model.entity.ImagemAcao;
import br.com.sol_do_amanhecer.model.mapper.AcaoMapper;
import br.com.sol_do_amanhecer.model.mapper.ImagemAcaoMapper;
import br.com.sol_do_amanhecer.repository.AcaoRepository;
import br.com.sol_do_amanhecer.repository.ImagemAcaoRepository;
import br.com.sol_do_amanhecer.service.AcaoService;
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
public class AcaoServiceImpl implements AcaoService {

    private static final Logger LOGGER = LoggerFactory.getLogger(AcaoServiceImpl.class);

    private final AcaoRepository acaoRepository;
    private final ImagemAcaoRepository imagemAcaoRepository;

    private final AcaoMapper acaoMapper = AcaoMapper.INSTANCE;
    private final ImagemAcaoMapper imagemAcaoMapper = ImagemAcaoMapper.INSTANCE;

    @Override
    @Transactional
    public AcaoDTO criar(AcaoDTO acaoDTO, List<ImagemAcaoDTO> imagemDTOs) {
        LOGGER.info("Criando uma ação e suas imagens associadas");

        Acao acao = acaoMapper.dtoParaEntity(acaoDTO);
        Acao acaoSalva = acaoRepository.save(acao);

        imagemDTOs.forEach(imagemDto -> {
            ImagemAcao imagemAcao = imagemAcaoMapper.dtoParaEntity(imagemDto);
            imagemAcao.setAcao(acaoSalva);
            imagemAcaoRepository.save(imagemAcao);
        });

        return acaoMapper.entityParaDto(acaoSalva);
    }

    @Override
    @Transactional
    public void atualizar(UUID id, AcaoDTO acaoDTO, List<ImagemAcaoDTO> imagemDTOs) {
        LOGGER.info("Atualizando a ação com ID: {}", id);

        Acao acaoExistente = acaoRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Ação não encontrada"));

        acaoExistente.setNome(acaoDTO.getNome());
        acaoExistente.setDescricao(acaoDTO.getDescricao());
        acaoExistente.setDataAcao(acaoDTO.getDataAcao());
        acaoExistente.setTipo(acaoDTO.getTipo());

        atualizarImagens(acaoExistente, imagemDTOs);

        acaoRepository.save(acaoExistente);
    }

    @Override
    @Transactional
    public void remover(UUID id) {
        LOGGER.info("Removendo a ação com ID: {}", id);

        Acao acaoExistente = acaoRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Ação não encontrada"));

        imagemAcaoRepository.deleteAllByAcao(acaoExistente);
        acaoRepository.deleteById(id);
    }

    @Override
    public AcaoResponseDTO buscarPorId(UUID id) {
        LOGGER.info("Buscando ação com ID: {}", id);

        Acao acao = acaoRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Ação não encontrada"));

        List<ImagemAcao> imagens = imagemAcaoRepository.findByAcao(acao);

        AcaoResponseDTO acaoResponseDTO = AcaoResponseDTO.builder()
                .acaoDTO(acaoMapper.entityParaDto(acao))
                .imagemDTOList(imagens.stream()
                        .map(imagemAcaoMapper::entityParaDto)
                        .collect(Collectors.toList()))
                .build();

        return acaoResponseDTO;
    }

    @Override
    public List<AcaoResponseDTO> buscarTodos() {
        LOGGER.info("Buscando todas as ações");

        List<Acao> acoes = acaoRepository.findAll();

        return acoes.stream().map(acao -> {
            AcaoResponseDTO acaoResponseDTO = new AcaoResponseDTO();

            acaoResponseDTO.setAcaoDTO(acaoMapper.entityParaDto(acao));

            List<ImagemAcao> imagens = imagemAcaoRepository.findByAcao(acao);

           acaoResponseDTO.setImagemDTOList(imagens.stream().map(imagemAcaoMapper::entityParaDto).collect(Collectors.toList()));

            return acaoResponseDTO;
        }).collect(Collectors.toList());
    }

    private void atualizarImagens(Acao acaoExistente, List<ImagemAcaoDTO> imagemDTOs) {
        imagemAcaoRepository.deleteAllByAcao(acaoExistente);

        imagemDTOs.forEach(imagemDTO -> {
            ImagemAcao imagemAcao = imagemAcaoMapper.dtoParaEntity(imagemDTO);
            imagemAcao.setAcao(acaoExistente);
            imagemAcaoRepository.save(imagemAcao);
        });
    }
}
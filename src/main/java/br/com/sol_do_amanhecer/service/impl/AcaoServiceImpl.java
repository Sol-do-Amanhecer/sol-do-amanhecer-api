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
import br.com.sol_do_amanhecer.shared.enums.ETipoAcao;
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
        acaoExistente.setLocalAcao(acaoDTO.getLocalAcao());
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
    public Page<AcaoResponseDTO> buscarTodos(ETipoAcao tipo, Integer ano, Integer mes, Pageable pageable) {
        Page<Acao> acoes;

        if (tipo != null && ano != null && mes != null) {
            LOGGER.info("Buscando ações filtradas por tipo: {}, ano: {} e mês: {}", tipo, ano, mes);
            acoes = acaoRepository.findByTipoAndAnoAndMes(tipo, ano, mes, pageable);
        } else if (ano != null && mes != null) {
            LOGGER.info("Buscando ações filtradas por ano: {} e mês: {}", ano, mes);
            acoes = acaoRepository.findByAnoAndMes(ano, mes, pageable);
        } else if (tipo != null && ano != null) {
            LOGGER.info("Buscando ações filtradas por tipo: {} e ano: {}", tipo, ano);
            acoes = acaoRepository.findByTipoAndAno(tipo, ano, pageable);
        } else if (tipo != null) {
            LOGGER.info("Buscando ações filtradas por tipo: {}", tipo);
            acoes = acaoRepository.findByTipo(tipo, pageable);
        } else if (ano != null) {
            LOGGER.info("Buscando ações filtradas por ano: {}", ano);
            acoes = acaoRepository.findByAno(ano, pageable);
        } else if (mes != null) {
            LOGGER.info("Buscando ações filtradas por mês: {}", mes);
            acoes = acaoRepository.findByMes(mes, pageable);
        } else {
            LOGGER.info("Buscando todas as ações sem filtros");
            acoes = acaoRepository.findAll(pageable);
        }

        return acoes.map(acao -> {
            AcaoResponseDTO acaoResponseDTO = new AcaoResponseDTO();
            acaoResponseDTO.setAcaoDTO(acaoMapper.entityParaDto(acao));

            List<ImagemAcao> imagens = imagemAcaoRepository.findByAcao(acao);
            acaoResponseDTO.setImagemDTOList(imagens.stream()
                    .map(imagemAcaoMapper::entityParaDto)
                    .collect(Collectors.toList()));

            return acaoResponseDTO;
        });
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
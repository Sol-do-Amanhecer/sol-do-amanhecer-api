package br.com.sol_do_amanhecer.service.impl;

import br.com.sol_do_amanhecer.model.dto.PrestacaoContasDTO;
import br.com.sol_do_amanhecer.model.entity.PrestacaoContas;
import br.com.sol_do_amanhecer.model.mapper.PrestacaoContasMapper;
import br.com.sol_do_amanhecer.repository.PrestacaoContasRepository;
import br.com.sol_do_amanhecer.service.PrestacaoContasService;
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
public class PrestacaoContasServiceImpl implements PrestacaoContasService {

    private final Logger LOGGER = LoggerFactory.getLogger(PrestacaoContasServiceImpl.class);
    private final PrestacaoContasRepository prestacaoContasRepository;
    private final PrestacaoContasMapper prestacaoContasMapper = PrestacaoContasMapper.INSTANCE;

    @Override
    @Transactional
    public PrestacaoContasDTO criar(PrestacaoContasDTO prestacaoContasDTO) {
        LOGGER.info("Criando uma prestação de contas");
        PrestacaoContas prestacaoContas = prestacaoContasMapper.dtoParaEntity(prestacaoContasDTO);
        PrestacaoContas prestacaoSalva = prestacaoContasRepository.save(prestacaoContas);
        return prestacaoContasMapper.entityParaDto(prestacaoSalva);
    }

    @Override
    @Transactional
    public void atualizar(UUID id, PrestacaoContasDTO prestacaoContasDTO) {
        LOGGER.info("Atualizando prestação de contas com ID: {}", id);
        PrestacaoContas prestacaoExistente = prestacaoContasRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Prestação de contas não encontrada"));
        prestacaoExistente.setDataTransacao(prestacaoContasDTO.getDataTransacao());
        prestacaoExistente.setDescricaoGasto(prestacaoContasDTO.getDescricaoGasto());
        prestacaoExistente.setDestinoGasto(prestacaoContasDTO.getDestinoGasto());
        prestacaoExistente.setValorPago(prestacaoContasDTO.getValorPago());
        prestacaoExistente.setEstabelecimento(prestacaoContasDTO.getEstabelecimento());
        prestacaoExistente.setNotaFiscal(prestacaoContasDTO.getNotaFiscal());
        prestacaoExistente.setComprovante(prestacaoContasDTO.getComprovante());
        prestacaoContasRepository.save(prestacaoExistente);
    }

    @Override
    @Transactional
    public void remover(UUID id) {
        LOGGER.info("Removendo prestação de contas com ID: {}", id);
        if (!prestacaoContasRepository.existsById(id)) {
            throw new RuntimeException("Prestação de contas não encontrada");
        }
        prestacaoContasRepository.deleteById(id);
    }

    @Override
    public PrestacaoContasDTO buscarPorId(UUID id) {
        LOGGER.info("Buscando prestação de contas com ID: {}", id);
        PrestacaoContas prestacaoContas = prestacaoContasRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Prestação de contas não encontrada"));
        return prestacaoContasMapper.entityParaDto(prestacaoContas);
    }

    @Override
    public Page<PrestacaoContasDTO> buscarTodas(Integer mes, Integer ano, Pageable pageable) {
        LOGGER.info("Buscando prestações de contas com filtro: mês={}, ano={}", mes, ano);

        Page<PrestacaoContas> prestacoes;

        if (mes != null && ano != null) {
            LOGGER.info("Filtrando por mês e ano");
            prestacoes = prestacaoContasRepository.findByMesEAno(mes, ano, pageable);
        } else if (mes != null) {
            LOGGER.info("Filtrando apenas por mês");
            prestacoes = prestacaoContasRepository.findByMes(mes, pageable);
        } else if (ano != null) {
            LOGGER.info("Filtrando apenas por ano");
            prestacoes = prestacaoContasRepository.findByAno(ano, pageable);
        } else {
            LOGGER.info("Nenhum filtro aplicado, retornando todas as prestações");
            prestacoes = prestacaoContasRepository.findAll(pageable);
        }

        return prestacoes.map(prestacaoContasMapper::entityParaDto);
    }
}
package br.com.sol_do_amanhecer.service.impl;

import br.com.sol_do_amanhecer.model.dto.DoacaoDTO;
import br.com.sol_do_amanhecer.model.entity.Doacao;
import br.com.sol_do_amanhecer.model.mapper.DoacaoMapper;
import br.com.sol_do_amanhecer.repository.DoacaoRepository;
import br.com.sol_do_amanhecer.service.DoacaoService;
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
public class DoacaoServiceImpl implements DoacaoService {

    private final Logger LOGGER = LoggerFactory.getLogger(DoacaoServiceImpl.class);
    private final DoacaoRepository doacaoRepository;
    private final DoacaoMapper doacaoMapper = DoacaoMapper.INSTANCE;

    @Override
    @Transactional
    public DoacaoDTO criar(DoacaoDTO doacaoDTO) {
        LOGGER.info("Criando uma doação");
        Doacao doacao = doacaoMapper.dtoParaEntity(doacaoDTO);
        Doacao doacaoSalva = doacaoRepository.save(doacao);
        return doacaoMapper.entityParaDto(doacaoSalva);
    }

    @Override
    @Transactional
    public void atualizar(UUID id, DoacaoDTO doacaoDTO) {
        LOGGER.info("Atualizando uma doação com ID: {}", id);
        Doacao doacaoExistente = doacaoRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Doação não encontrada"));
        doacaoExistente.setDataDoacao(doacaoDTO.getDataDoacao());
        doacaoExistente.setNomeDoador(doacaoDTO.getNomeDoador());
        doacaoExistente.setMeioDoacao(doacaoDTO.getMeioDoacao());
        doacaoExistente.setValor(doacaoDTO.getValor());
        doacaoExistente.setComprovante(doacaoDTO.getComprovante());
        doacaoRepository.save(doacaoExistente);
    }

    @Override
    @Transactional
    public void remover(UUID id) {
        LOGGER.info("Removendo uma doação com ID: {}", id);
        if (!doacaoRepository.existsById(id)) {
            throw new RuntimeException("Doação não encontrada");
        }
        doacaoRepository.deleteById(id);
    }

    @Override
    public DoacaoDTO buscarPorId(UUID id) {
        LOGGER.info("Buscando uma doação com ID: {}", id);
        Doacao doacao = doacaoRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Doação não encontrada"));
        return doacaoMapper.entityParaDto(doacao);
    }

    @Override
    public List<DoacaoDTO> buscarTodas() {
        LOGGER.info("Buscando todas as doações");
        List<Doacao> doacoes = doacaoRepository.findAll();
        return doacoes.stream().map(doacaoMapper::entityParaDto).collect(Collectors.toList());
    }
}
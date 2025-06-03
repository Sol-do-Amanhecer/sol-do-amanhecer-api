package br.com.sol_do_amanhecer.service.impl;

import br.com.sol_do_amanhecer.exception.PermissaoException;
import br.com.sol_do_amanhecer.model.dto.PermissaoDTO;
import br.com.sol_do_amanhecer.model.entity.Permissao;
import br.com.sol_do_amanhecer.model.mapper.PermissaoMapper;
import br.com.sol_do_amanhecer.repository.PermissaoRepository;
import br.com.sol_do_amanhecer.service.PermissaoService;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class PermissaoServiceImpl implements PermissaoService {

    private final Logger LOGGER = LoggerFactory.getLogger(PermissaoServiceImpl.class);
    private final PermissaoRepository permissaoRepository;
    private final PermissaoMapper permissaoMapper = PermissaoMapper.INSTANCE;

    @Override
    public PermissaoDTO buscarPorId(UUID id) {
        LOGGER.info("Buscando permissão por id");
        Permissao permissaoEntity = this.permissaoRepository
                .findById(id)
                .orElseThrow(() -> new PermissaoException("Permissão não encontrada com ID: " + id));
        return permissaoMapper.entityParaDto(permissaoEntity);
    }

    @Override
    public List<PermissaoDTO> buscarTodos() {
        LOGGER.info("Buscar todas as permissões");
        return this.permissaoRepository
                .findAll()
                .stream()
                .map(permissaoMapper::entityParaDto)
                .toList();
    }

    @Override
    public PermissaoDTO criar(PermissaoDTO permissaoDTO) {
        LOGGER.info("Criando uma permissão");
        Permissao permissaoEntity = permissaoMapper.dtoParaEntity(permissaoDTO);
        this.permissaoRepository.save(permissaoEntity);
        return permissaoMapper.entityParaDto(permissaoEntity);
    }

    @Override
    public void atualizar(UUID id, PermissaoDTO permissaoDTO) {
        LOGGER.info("Atualizar uma permissão");
        Permissao permissaoEntity = this.permissaoRepository
                .findById(id)
                .orElseThrow(() -> new PermissaoException("Permissão não encontrada com ID: " + id));
        permissaoEntity.setDescricao(permissaoDTO.getDescricao());
        permissaoRepository.save(permissaoEntity);
    }

    @Override
    public void remover(UUID id) {
        LOGGER.info("Remover uma permissão");
        Permissao permissaoEntity = this.permissaoRepository
                .findById(id)
                .orElseThrow(() -> new PermissaoException("Permissão não encontrada com ID: " + id));
        this.permissaoRepository.delete(permissaoEntity);
    }
}
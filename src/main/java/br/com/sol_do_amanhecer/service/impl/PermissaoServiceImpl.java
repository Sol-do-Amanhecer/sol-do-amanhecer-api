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

    @Override
    public PermissaoDTO buscarPorId(UUID id) {
        LOGGER.info("Buscando permissão por id");
        Permissao PermissaoEntity = this.permissaoRepository
                .findById(id)
                .orElseThrow(() -> new PermissaoException("Permissao not found with ID: " + id));
        return PermissaoMapper.INSTANCE.entityParaDto(PermissaoEntity);
    }

    @Override
    public List<PermissaoDTO> buscarTodos() {
        LOGGER.info("Buscar todas as permissões");
        return this.permissaoRepository
                .findAll()
                .stream()
                .map(PermissaoMapper.INSTANCE::entityParaDto)
                .toList();
    }

    @Override
    public PermissaoDTO criar(PermissaoDTO PermissaoDTO) {
        LOGGER.info("Criando uma permissão");
        Permissao PermissaoEntity = PermissaoMapper.INSTANCE.dtoParaEntity(PermissaoDTO);
        this.permissaoRepository.save(PermissaoEntity);
        return PermissaoMapper.INSTANCE.entityParaDto(PermissaoEntity);
    }

    @Override
    public void atualizar(UUID id, PermissaoDTO PermissaoDTO) {
        LOGGER.info("Atualizar uma permissão");
        Permissao PermissaoEntity = this.permissaoRepository
                .findById(id)
                .orElseThrow(() -> new PermissaoException("Permissao not found with ID: " + id));
        PermissaoEntity.setDescricao(PermissaoDTO.getDescricao());
        permissaoRepository.save(PermissaoEntity);
    }

    @Override
    public void remover(UUID id) {
        LOGGER.info("Remover uma permissão");
        Permissao PermissaoEntity = this.permissaoRepository
                .findById(id)
                .orElseThrow(() -> new PermissaoException("Permissao not found with ID: " + id));
        this.permissaoRepository.delete(PermissaoEntity);
    }
}

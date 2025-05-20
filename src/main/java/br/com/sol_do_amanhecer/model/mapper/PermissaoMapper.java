package br.com.sol_do_amanhecer.model.mapper;

import br.com.sol_do_amanhecer.model.dto.PermissaoDTO;
import br.com.sol_do_amanhecer.model.entity.Permissao;
import org.mapstruct.Mapper;
import org.mapstruct.factory.Mappers;

@Mapper
public interface PermissaoMapper {
    PermissaoMapper INSTANCE = Mappers.getMapper(PermissaoMapper.class);

    Permissao dtoParaEntity(PermissaoDTO permissaoDTO);

    PermissaoDTO entityParaDto(Permissao permissao);
}

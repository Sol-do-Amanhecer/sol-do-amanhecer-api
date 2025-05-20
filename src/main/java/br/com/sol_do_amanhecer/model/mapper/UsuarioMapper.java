package br.com.sol_do_amanhecer.model.mapper;

import br.com.sol_do_amanhecer.model.dto.UsuarioDTO;
import br.com.sol_do_amanhecer.model.entity.Usuario;
import org.mapstruct.Mapper;
import org.mapstruct.factory.Mappers;

@Mapper
public interface UsuarioMapper {
    UsuarioMapper INSTANCE = Mappers.getMapper(UsuarioMapper.class);

    Usuario dtoParaEntity(UsuarioDTO usuarioDTO);

    UsuarioDTO entityParaDto(Usuario usuario);
}

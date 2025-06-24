package br.com.sol_do_amanhecer.model.mapper;

import br.com.sol_do_amanhecer.model.dto.UsuarioDTO;
import br.com.sol_do_amanhecer.model.dto.UsuarioStatusIdDTO;
import br.com.sol_do_amanhecer.model.entity.Usuario;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.factory.Mappers;

@Mapper(componentModel = "spring")
public interface UsuarioMapper {
    UsuarioMapper INSTANCE = Mappers.getMapper(UsuarioMapper.class);

    @Mapping(source = "permissaoDTOList", target = "permissoes")
    @Mapping(source = "uuidVoluntario", target = "voluntario.uuid")
    Usuario dtoParaEntity(UsuarioDTO usuarioDTO);

    @Mapping(source = "permissoes", target = "permissaoDTOList")
    @Mapping(source = "voluntario.uuid", target = "uuidVoluntario")
    UsuarioDTO entityParaDto(Usuario usuario);

    UsuarioStatusIdDTO entityParaDtoStatus(Usuario usuario);
}

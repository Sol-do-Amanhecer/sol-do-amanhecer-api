package br.com.sol_do_amanhecer.model.mapper;

import br.com.sol_do_amanhecer.model.dto.VoluntarioDTO;
import br.com.sol_do_amanhecer.model.entity.Voluntario;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.factory.Mappers;

@Mapper
public interface VoluntarioMapper {
    VoluntarioMapper INSTANCE = Mappers.getMapper(VoluntarioMapper.class);

    @Mapping(source = "enderecoDTO", target = "endereco")
    Voluntario dtoParaEntity(VoluntarioDTO voluntarioDTO);

    @Mapping(source = "endereco", target = "enderecoDTO")
    VoluntarioDTO entityParaDto(Voluntario voluntario);
}

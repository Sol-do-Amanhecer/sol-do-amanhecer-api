package br.com.sol_do_amanhecer.model.mapper;

import br.com.sol_do_amanhecer.model.dto.TelefoneDTO;
import br.com.sol_do_amanhecer.model.entity.Telefone;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.factory.Mappers;

@Mapper
public interface TelefoneMapper {
    TelefoneMapper INSTANCE = Mappers.getMapper(TelefoneMapper.class);

    @Mapping(source = "uuidVoluntario", target = "voluntario.uuid")
    Telefone dtoParaEntity(TelefoneDTO telefoneDTO);

    @Mapping(source = "voluntario.uuid", target = "uuidVoluntario")
    TelefoneDTO entityParaDto(Telefone telefone);
}

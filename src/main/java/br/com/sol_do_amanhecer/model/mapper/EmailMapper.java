package br.com.sol_do_amanhecer.model.mapper;

import br.com.sol_do_amanhecer.model.dto.EmailDTO;
import br.com.sol_do_amanhecer.model.entity.Email;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.factory.Mappers;

@Mapper
public interface EmailMapper {
    EmailMapper INSTANCE = Mappers.getMapper(EmailMapper.class);

    @Mapping(source = "voluntarioDTO", target = "voluntario")
    Email dtoParaEntity(EmailDTO emailDTO);

    @Mapping(source = "voluntario", target = "voluntarioDTO")
    EmailDTO entityParaDto(Email email);
}

package br.com.sol_do_amanhecer.model.mapper;

import br.com.sol_do_amanhecer.model.dto.EmailDTO;
import br.com.sol_do_amanhecer.model.dto.FormularioVoluntarioDTO;
import br.com.sol_do_amanhecer.model.entity.Email;
import br.com.sol_do_amanhecer.model.entity.FormularioVoluntario;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.factory.Mappers;

@Mapper
public interface EmailMapper {
    EmailMapper INSTANCE = Mappers.getMapper(EmailMapper.class);

    @Mapping(source = "uuidVoluntario", target = "voluntario.uuid")
    Email dtoParaEntity(EmailDTO emailDTO);

    @Mapping(source = "voluntario.uuid", target = "uuidVoluntario")
    EmailDTO entityParaDto(Email email);
}

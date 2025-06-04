package br.com.sol_do_amanhecer.model.mapper;

import br.com.sol_do_amanhecer.model.dto.FormularioVoluntarioDTO;
import br.com.sol_do_amanhecer.model.entity.FormularioVoluntario;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.factory.Mappers;

@Mapper
public interface FormularioVoluntarioMapper {
    FormularioVoluntarioMapper INSTANCE = Mappers.getMapper(FormularioVoluntarioMapper.class);

    @Mapping(source = "uuidVoluntario", target = "voluntario.uuid")
    FormularioVoluntario dtoParaEntity(FormularioVoluntarioDTO formularioVoluntarioDTO);

    @Mapping(source = "voluntario.uuid", target = "uuidVoluntario")
    FormularioVoluntarioDTO entityParaDto(FormularioVoluntario formularioVoluntario);
}
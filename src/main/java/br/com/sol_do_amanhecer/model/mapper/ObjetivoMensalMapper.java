package br.com.sol_do_amanhecer.model.mapper;

import br.com.sol_do_amanhecer.model.dto.ObjetivoMensalDTO;
import br.com.sol_do_amanhecer.model.entity.ObjetivoMensal;
import org.mapstruct.Mapper;
import org.mapstruct.factory.Mappers;

@Mapper
public interface ObjetivoMensalMapper {

    ObjetivoMensalMapper INSTANCE = Mappers.getMapper(ObjetivoMensalMapper.class);

    ObjetivoMensal dtoParaEntity(ObjetivoMensalDTO objetivoMensalDTO);

    ObjetivoMensalDTO entityParaDto(ObjetivoMensal objetivoMensal);
}
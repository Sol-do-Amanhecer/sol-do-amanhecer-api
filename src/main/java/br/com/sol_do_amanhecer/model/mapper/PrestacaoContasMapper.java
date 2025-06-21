package br.com.sol_do_amanhecer.model.mapper;

import br.com.sol_do_amanhecer.model.dto.PrestacaoContasDTO;
import br.com.sol_do_amanhecer.model.entity.PrestacaoContas;
import org.mapstruct.Mapper;
import org.mapstruct.factory.Mappers;

@Mapper
public interface PrestacaoContasMapper {

    PrestacaoContasMapper INSTANCE = Mappers.getMapper(PrestacaoContasMapper.class);

    PrestacaoContas dtoParaEntity(PrestacaoContasDTO prestacaoContasDTO);

    PrestacaoContasDTO entityParaDto(PrestacaoContas prestacaoContas);
}
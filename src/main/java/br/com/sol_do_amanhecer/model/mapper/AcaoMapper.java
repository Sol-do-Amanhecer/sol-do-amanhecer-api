package br.com.sol_do_amanhecer.model.mapper;

import br.com.sol_do_amanhecer.model.dto.AcaoDTO;
import br.com.sol_do_amanhecer.model.entity.Acao;
import org.mapstruct.*;
import org.mapstruct.factory.Mappers;

@Mapper
public interface AcaoMapper {
    AcaoMapper INSTANCE = Mappers.getMapper(AcaoMapper.class);

    Acao dtoParaEntity(AcaoDTO acaoDTO);

    AcaoDTO entityParaDto(Acao acao);
}

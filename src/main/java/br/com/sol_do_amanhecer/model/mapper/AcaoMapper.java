package br.com.sol_do_amanhecer.model.mapper;

import br.com.sol_do_amanhecer.model.dto.AcaoDTO;
import br.com.sol_do_amanhecer.model.entity.Acao;
import org.mapstruct.*;
import org.mapstruct.factory.Mappers;

@Mapper
public interface AcaoMapper {
    AcaoMapper INSTANCE = Mappers.getMapper(AcaoMapper.class);

    @Mapping(source = "imagens", target = "imagens")
    Acao dtoParaEntity(AcaoDTO acaoDTO);

    @Mapping(source = "imagens", target = "imagens")
    AcaoDTO entityParaDto(Acao acao);
}

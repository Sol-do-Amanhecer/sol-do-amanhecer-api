package br.com.sol_do_amanhecer.model.mapper;

import br.com.sol_do_amanhecer.model.dto.ImagemAcaoDTO;
import br.com.sol_do_amanhecer.model.entity.ImagemAcao;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.factory.Mappers;

@Mapper
public interface ImagemAcaoMapper {

    ImagemAcaoMapper INSTANCE = Mappers.getMapper(ImagemAcaoMapper.class);

    @Mapping(source = "uuidAcao", target = "acao.uuid")
    ImagemAcao dtoParaEntity(ImagemAcaoDTO dto);

    @Mapping(source = "acao.uuid", target = "uuidAcao")
    ImagemAcaoDTO entityParaDto(ImagemAcao entity);
}

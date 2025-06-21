package br.com.sol_do_amanhecer.model.mapper;

import br.com.sol_do_amanhecer.model.dto.DoacaoDTO;
import br.com.sol_do_amanhecer.model.entity.Doacao;
import org.mapstruct.Mapper;
import org.mapstruct.factory.Mappers;

@Mapper
public interface DoacaoMapper {

    DoacaoMapper INSTANCE = Mappers.getMapper(DoacaoMapper.class);

    Doacao dtoParaEntity(DoacaoDTO doacaoDTO);

    DoacaoDTO entityParaDto(Doacao doacao);
}
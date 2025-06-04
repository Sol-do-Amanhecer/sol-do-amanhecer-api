package br.com.sol_do_amanhecer.model.mapper;

import br.com.sol_do_amanhecer.model.dto.EnderecoDTO;
import br.com.sol_do_amanhecer.model.entity.Endereco;
import org.mapstruct.Mapper;
import org.mapstruct.factory.Mappers;

@Mapper
public interface EnderecoMapper {
    EnderecoMapper INSTANCE = Mappers.getMapper(EnderecoMapper.class);

    Endereco dtoParaEntity(EnderecoDTO enderecoDTO);

    EnderecoDTO entityParaDto(Endereco endereco);
}

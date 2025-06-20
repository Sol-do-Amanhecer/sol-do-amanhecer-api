package br.com.sol_do_amanhecer.service.impl;

import br.com.sol_do_amanhecer.model.dto.AcaoDTO;
import br.com.sol_do_amanhecer.model.dto.ImagemAcaoDTO;
import br.com.sol_do_amanhecer.model.entity.Acao;
import br.com.sol_do_amanhecer.model.entity.ImagemAcao;
import br.com.sol_do_amanhecer.model.mapper.AcaoMapper;
import br.com.sol_do_amanhecer.repository.AcaoRepository;
import br.com.sol_do_amanhecer.repository.ImagemAcaoRepository;
import br.com.sol_do_amanhecer.service.AcaoService;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class AcaoServiceImpl implements AcaoService {
    private static final Logger LOGGER = LoggerFactory.getLogger(AcaoServiceImpl.class.getName());

    private final AcaoRepository acaoRepository;
    private final ImagemAcaoRepository imagemAcaoRepository;

    private final AcaoMapper acaoMapper = AcaoMapper.INSTANCE;

    @Override
    public AcaoDTO buscarPorId(UUID uuid) {
        Acao acao = acaoRepository
                .findById(uuid)
                .orElseThrow(() -> new RuntimeException("A ação não foi encontrada"));

        return acaoMapper.entityParaDto(acao);
    }

    @Override
    public List<AcaoDTO> buscarTodos() {
        var acoes = acaoRepository.findAll();
        return acoes
                .stream()
                .map(acaoMapper::entityParaDto)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional
    public AcaoDTO criar(AcaoDTO acaoDTO) {
        Acao acao = acaoMapper.dtoParaEntity(acaoDTO);
        return acaoMapper.entityParaDto(acaoRepository.save(acao));
    }

    @Override
    @Transactional
    public void atualizar(UUID uuid, AcaoDTO acaoDTO) {
        Acao acaoExistente = acaoRepository.findById(uuid)
                .orElseThrow(() -> new RuntimeException("Ação a ser atualizada não foi encontrada"));

        acaoExistente.setNome(acaoDTO.getNome());
        acaoExistente.setDescricao(acaoDTO.getDescricao());
        acaoExistente.setDataAcao(acaoDTO.getDataAcao());
        acaoExistente.setTipo(acaoDTO.getTipo());

        List<ImagemAcao> imagensEntidade = new ArrayList<>(acaoExistente.getImagens() != null ? acaoExistente.getImagens() : Collections.emptyList());

        List<byte[]> imagensDTO = acaoDTO.getImagens().stream()
                .map(ImagemAcaoDTO::getImagem)
                .collect(Collectors.toList());

        List<ImagemAcao> imagensASeremRemovidas = imagensEntidade.stream()
                .filter(imgEnt -> imagensDTO.stream().noneMatch(imgDTO -> Arrays.equals(imgEnt.getImagem(), imgDTO)))
                .collect(Collectors.toList());

        List<ImagemAcao> imagensMantidas = imagensEntidade.stream()
                .filter(imgEnt -> imagensDTO.stream().anyMatch(imgDTO -> Arrays.equals(imgEnt.getImagem(), imgDTO)))
                .collect(Collectors.toList());

        List<byte[]> imagensParaAdicionar = imagensDTO.stream()
                .filter(imgDTO -> imagensEntidade.stream().noneMatch(imgEnt -> Arrays.equals(imgEnt.getImagem(), imgDTO)))
                .collect(Collectors.toList());

        for (byte[] imagemNova : imagensParaAdicionar) {
            ImagemAcao novaImagem = new ImagemAcao();
            novaImagem.setImagem(imagemNova);
            imagensMantidas.add(novaImagem);
        }

        acaoExistente.setImagens(imagensMantidas);

        imagensASeremRemovidas.forEach(imagemAcaoRepository::delete);

        acaoRepository.save(acaoExistente);
    }

    @Override
    @Transactional
    public void remover(AcaoDTO acaoDTO) {
        acaoRepository.deleteById(acaoDTO.getUuid());
    }
}

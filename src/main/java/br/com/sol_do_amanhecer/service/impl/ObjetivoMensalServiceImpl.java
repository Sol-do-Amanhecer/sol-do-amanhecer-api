package br.com.sol_do_amanhecer.service.impl;

import br.com.sol_do_amanhecer.model.dto.ObjetivoMensalDTO;
import br.com.sol_do_amanhecer.model.dto.ObjetivoMensalRequestDTO;
import br.com.sol_do_amanhecer.model.entity.ObjetivoMensal;
import br.com.sol_do_amanhecer.model.mapper.ObjetivoMensalMapper;
import br.com.sol_do_amanhecer.repository.DoacaoRepository;
import br.com.sol_do_amanhecer.repository.ObjetivoMensalRepository;
import br.com.sol_do_amanhecer.repository.PrestacaoContasRepository;
import br.com.sol_do_amanhecer.service.ObjetivoMensalService;
import br.com.sol_do_amanhecer.shared.enums.EMes;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class ObjetivoMensalServiceImpl implements ObjetivoMensalService {

    private final Logger LOGGER = LoggerFactory.getLogger(ObjetivoMensalServiceImpl.class);
    private final ObjetivoMensalRepository objetivoMensalRepository;
    private final DoacaoRepository doacaoRepository;
    private final PrestacaoContasRepository prestacaoContasRepository;
    private final ObjetivoMensalMapper mapper = ObjetivoMensalMapper.INSTANCE;

    @Override
    @Transactional
    public ObjetivoMensalDTO criar(ObjetivoMensalRequestDTO requestDTO) {
        ObjetivoMensal objetivoMensal = ObjetivoMensal.builder()
                .titulo(requestDTO.getTitulo())
                .descricao(requestDTO.getDescricao())
                .mes(requestDTO.getMes())
                .ano(requestDTO.getAno())
                .objetivoArrecadacao(requestDTO.getObjetivoArrecadacao())
                .build();

        objetivoMensal = objetivoMensalRepository.save(objetivoMensal);
        return mapper.entityParaDto(objetivoMensal);
    }

    @Override
    @Transactional
    public void atualizar(UUID id, ObjetivoMensalRequestDTO requestDTO) {
        ObjetivoMensal objetivo = objetivoMensalRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Objetivo Mensal não encontrado"));

        objetivo.setTitulo(requestDTO.getTitulo());
        objetivo.setDescricao(requestDTO.getDescricao());
        objetivo.setMes(requestDTO.getMes());
        objetivo.setAno(requestDTO.getAno());
        objetivo.setObjetivoArrecadacao(requestDTO.getObjetivoArrecadacao());

        objetivoMensalRepository.save(objetivo);
    }

    @Override
    public ObjetivoMensalDTO buscarPorId(UUID id) {
        ObjetivoMensal objetivo = objetivoMensalRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Objetivo Mensal não encontrado"));

        calcularValoresDinamicos(objetivo);
        return mapper.entityParaDto(objetivo);
    }

    @Override
    public Page<ObjetivoMensalDTO> buscarTodos(EMes mes, Integer ano, Pageable pageable) {
        LOGGER.info("Buscando objetivos mensais com filtros: mes={}, ano={}", mes, ano);

        Page<ObjetivoMensal> objetivos;

        if (mes != null && ano != null) {
            LOGGER.info("Filtrando por mês e ano");
            objetivos = objetivoMensalRepository.findByMesAndAno(mes, ano, pageable);
        } else if (mes != null) {
            LOGGER.info("Filtrando apenas por mês");
            objetivos = objetivoMensalRepository.findByMes(mes, pageable);
        } else if (ano != null) {
            LOGGER.info("Filtrando apenas por ano");
            objetivos = objetivoMensalRepository.findByAno(ano, pageable);
        } else {
            LOGGER.info("Nenhum filtro aplicado, retornando todos os objetivos");
            objetivos = objetivoMensalRepository.findAll(pageable);
        }

        return objetivos.map(objetivo -> {
            calcularValoresDinamicos(objetivo);
            return mapper.entityParaDto(objetivo);
        });
    }

    @Override
    @Transactional
    public void remover(UUID id) {
        if (!objetivoMensalRepository.existsById(id)) {
            throw new RuntimeException("Objetivo Mensal não encontrado");
        }
        objetivoMensalRepository.deleteById(id);
    }

    private void calcularValoresDinamicos(ObjetivoMensal objetivo) {
        LocalDate inicioDoMes = LocalDate.of(objetivo.getAno(), objetivo.getMes().getNumero(), 1);
        LocalDate fimDoMes = inicioDoMes.withDayOfMonth(inicioDoMes.lengthOfMonth());

        BigDecimal arrecadado = doacaoRepository.findTotalByPeriodo(inicioDoMes, fimDoMes);
        BigDecimal gasto = prestacaoContasRepository.findTotalByPeriodo(inicioDoMes, fimDoMes);

        Long quantidadeDoacoes = doacaoRepository.findCountByPeriodo(inicioDoMes, fimDoMes);
        Long quantidadePrestacoesContas = prestacaoContasRepository.findCountByPeriodo(inicioDoMes, fimDoMes);

        arrecadado = arrecadado != null ? arrecadado : BigDecimal.ZERO;
        gasto = gasto != null ? gasto : BigDecimal.ZERO;
        quantidadeDoacoes = quantidadeDoacoes != null ? quantidadeDoacoes : 0L;
        quantidadePrestacoesContas = quantidadePrestacoesContas != null ? quantidadePrestacoesContas : 0L;

        BigDecimal percentualProgresso = arrecadado.compareTo(BigDecimal.ZERO) > 0 && objetivo.getObjetivoArrecadacao().compareTo(BigDecimal.ZERO) > 0
                ? (arrecadado.divide(objetivo.getObjetivoArrecadacao())).multiply(BigDecimal.valueOf(100))
                : BigDecimal.ZERO;

        objetivo.setArrecadado(arrecadado);
        objetivo.setGasto(gasto);
        objetivo.setPercentualProgresso(percentualProgresso);
        objetivo.setQuantidadeDoacao(quantidadeDoacoes.intValue());
        objetivo.setQuantidadePrestacaoConta(quantidadePrestacoesContas.intValue());
    }
}
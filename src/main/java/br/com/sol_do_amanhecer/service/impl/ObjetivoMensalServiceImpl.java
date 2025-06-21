package br.com.sol_do_amanhecer.service.impl;

import br.com.sol_do_amanhecer.model.dto.ObjetivoMensalDTO;
import br.com.sol_do_amanhecer.model.dto.ObjetivoMensalRequestDTO;
import br.com.sol_do_amanhecer.model.entity.ObjetivoMensal;
import br.com.sol_do_amanhecer.model.mapper.ObjetivoMensalMapper;
import br.com.sol_do_amanhecer.repository.DoacaoRepository;
import br.com.sol_do_amanhecer.repository.PrestacaoContasRepository;
import br.com.sol_do_amanhecer.repository.ObjetivoMensalRepository;
import br.com.sol_do_amanhecer.service.ObjetivoMensalService;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class ObjetivoMensalServiceImpl implements ObjetivoMensalService {

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
                .objetivoGastos(requestDTO.getObjetivoGastos())
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
        objetivo.setObjetivoGastos(requestDTO.getObjetivoGastos());

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
    public List<ObjetivoMensalDTO> buscarTodos() {
        List<ObjetivoMensal> objetivos = objetivoMensalRepository.findAll();

        return objetivos.stream()
                .map(objetivo -> {
                    calcularValoresDinamicos(objetivo);
                    return mapper.entityParaDto(objetivo);
                })
                .collect(Collectors.toList());
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

        Double arrecadado = doacaoRepository.findTotalByPeriodo(inicioDoMes, fimDoMes);
        Double gasto = prestacaoContasRepository.findTotalByPeriodo(inicioDoMes, fimDoMes);

        arrecadado = arrecadado != null ? arrecadado : 0.0;
        gasto = gasto != null ? gasto : 0.0;

        Double percentualProgresso = arrecadado > 0 && objetivo.getObjetivoArrecadacao() > 0
                ? (arrecadado / objetivo.getObjetivoArrecadacao()) * 100
                : 0.0;

        objetivo.setArrecadado(arrecadado);
        objetivo.setGasto(gasto);
        objetivo.setPercentualProgresso(percentualProgresso);
    }
}
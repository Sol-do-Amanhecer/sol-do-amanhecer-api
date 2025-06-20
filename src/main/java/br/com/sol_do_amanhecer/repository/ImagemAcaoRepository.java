package br.com.sol_do_amanhecer.repository;

import br.com.sol_do_amanhecer.model.entity.Acao;
import br.com.sol_do_amanhecer.model.entity.ImagemAcao;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

@Repository
public interface ImagemAcaoRepository extends JpaRepository<ImagemAcao, UUID> {

    void deleteAllByAcao(Acao acao);

    List<ImagemAcao> findByAcao(Acao acao);
}

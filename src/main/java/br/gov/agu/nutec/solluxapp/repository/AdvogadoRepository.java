package br.gov.agu.nutec.solluxapp.repository;

import br.gov.agu.nutec.solluxapp.entity.AdvogadoEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface AdvogadoRepository extends JpaRepository<AdvogadoEntity, Long> {


    boolean existsByNomeIn(List<String> advogadosNames);
}

package br.gov.agu.nutec.solluxapp.repository;

import br.gov.agu.nutec.solluxapp.entity.AdvogadoEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface AdvogadoRepository extends JpaRepository<AdvogadoEntity, Long> {


    @Query("SELECT COUNT(a) > 0 FROM AdvogadoEntity a WHERE a.nome IN :nomes AND a.isSuspeito = true")
    boolean existsSuspeitoByNomeIn(@Param("nomes") List<String> nomes);

}

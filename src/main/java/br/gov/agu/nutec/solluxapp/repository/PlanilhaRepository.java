package br.gov.agu.nutec.solluxapp.repository;

import br.gov.agu.nutec.solluxapp.entity.PlanilhaEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface PlanilhaRepository extends JpaRepository<PlanilhaEntity, Long> {

    boolean existsByHash(String hash);

}

package br.com.dbccompany.colaboreapi.repository;

import br.com.dbccompany.colaboreapi.entity.CampanhaEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface CampanhaRepository extends JpaRepository<CampanhaEntity, Integer> {
}

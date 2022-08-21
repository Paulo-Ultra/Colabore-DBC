package br.com.dbccompany.colaboreapi.repository;

import br.com.dbccompany.colaboreapi.entity.DoadorEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface DoadorRepository extends JpaRepository<DoadorEntity, Integer>{
}

package br.com.dbccompany.colaboreapi.repository;

import br.com.dbccompany.colaboreapi.entity.CampanhaEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface CampanhaRepository extends JpaRepository<CampanhaEntity, Integer> {

    @Query("SELECT p FROM campanha p WHERE p.idUsuario = ?1")
    List<CampanhaEntity> findAllByIdUsuario(Integer idJogador);
}

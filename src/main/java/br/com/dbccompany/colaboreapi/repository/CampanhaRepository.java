package br.com.dbccompany.colaboreapi.repository;

import br.com.dbccompany.colaboreapi.entity.CampanhaEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface CampanhaRepository extends JpaRepository<CampanhaEntity, Integer> {

   List<CampanhaEntity> findAllByIdUsuario(Integer idUsuario);
   List<CampanhaEntity> findAllByIdUsuarioAndIdCampanha(Integer idUsuario, Integer idCampanha);
   List<CampanhaEntity> findAllByStatusMeta(Boolean statusMeta);
}

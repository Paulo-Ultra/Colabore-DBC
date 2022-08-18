package br.com.dbccompany.colaboreapi.repository;

import br.com.dbccompany.colaboreapi.entity.CampanhaEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface CampanhaRepository extends JpaRepository<CampanhaEntity, Integer> {

   List<CampanhaEntity> findAllByIdUsuario(Integer idUsuario);
   List<CampanhaEntity> findAllByIdUsuarioAndIdCampanha(Integer idUsuario, Integer idCampanha);
   List<CampanhaEntity> findAllByStatusMeta(Boolean statusMeta);

      @Query("select c " +
              " from campanha c" +
              " left join c.doadores d " +
              " where (:minhasContribuicoes = false OR d.idUsuario = :idUsuario )  " +
              "   and (:minhasCampanhas = false OR c.idUsuario = :idUsuario)" )
      List<CampanhaEntity> findAll(@Param("idUsuario")Integer idUsuario,
                                   @Param("minhasContribuicoes") Boolean minhasContribuicoes,
                                   @Param("minhasCampanhas") Boolean minhasCampanhas);

}

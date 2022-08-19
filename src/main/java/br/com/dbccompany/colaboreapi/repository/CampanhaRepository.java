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

   @Query("select distinct c " +
           " from campanha c" +
           " left join c.doadores d " +
           " left join c.tagEntities t " +
           " where (:minhasContribuicoes = false OR d.idUsuario = :idUsuario )  " +
           "   and (t.idTag in :idTag OR :idTag is null) " +
           "   and (:minhasCampanhas = false OR c.idUsuario = :idUsuario) " +
           "   and (CURRENT_TIMESTAMP > c.dataLimite) " +
           "   and (:statusMeta is null OR c.statusMeta = :statusMeta) order by c.dataLimite asc" )
   List<CampanhaEntity> findAll(@Param("statusMeta") Boolean statusMeta,
                                @Param("idUsuario")Integer idUsuario,
                                @Param("idTag")List<Integer> idTag,
                                @Param("minhasContribuicoes") Boolean minhasContribuicoes,
                                @Param("minhasCampanhas") Boolean minhasCampanhas);



}

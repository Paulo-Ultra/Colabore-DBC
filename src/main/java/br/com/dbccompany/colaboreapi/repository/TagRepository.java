package br.com.dbccompany.colaboreapi.repository;

import br.com.dbccompany.colaboreapi.entity.TagEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface TagRepository extends JpaRepository<TagEntity, Integer> {

    @Query(value = " SELECT COUNT(*)" +
            " FROM tag t" +
            " WHERE nome_tag = :nomeTag")
    Integer findByNomeTag(@Param("nomeTag") String nomeTag);

    @Query(value = "SELECT t.* " +
            "FROM tag t " +
            "JOIN campanha_x_tag ct ON t.id_tag = ct.id_tag " +
            "WHERE ct.id_campanha = :idCampanha"
            , nativeQuery = true
    )
    List<TagEntity> listTagCampanha(Integer idCampanha);
}

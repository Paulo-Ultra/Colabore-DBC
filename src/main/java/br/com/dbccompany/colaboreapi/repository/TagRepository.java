package br.com.dbccompany.colaboreapi.repository;

import br.com.dbccompany.colaboreapi.entity.TagEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface TagRepository extends JpaRepository<TagEntity, Integer> {

    @Query(value = " SELECT COUNT(*)" +
            " FROM tag t" +
            " WHERE nome_tag = :nomeTag")
    Integer findByNomeTagCount(@Param("nomeTag") String nomeTag);

    Optional<TagEntity> findByNomeTag (String nomeTag);
}

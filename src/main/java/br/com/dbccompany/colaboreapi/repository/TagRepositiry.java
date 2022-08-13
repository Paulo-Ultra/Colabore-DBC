package br.com.dbccompany.colaboreapi.repository;

import br.com.dbccompany.colaboreapi.entity.TagEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface TagRepositiry extends JpaRepository<TagEntity, Integer> {
}

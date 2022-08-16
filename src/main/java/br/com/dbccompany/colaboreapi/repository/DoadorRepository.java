package br.com.dbccompany.colaboreapi.repository;

import br.com.dbccompany.colaboreapi.entity.DoadorEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface DoadorRepository extends JpaRepository<DoadorEntity, Integer>{
    List<DoadorEntity> findByIdUsuario(Integer idUsuario);
}

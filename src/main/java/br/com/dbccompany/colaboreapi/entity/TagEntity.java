package br.com.dbccompany.colaboreapi.entity;

import lombok.Getter;
import lombok.Setter;

import javax.persistence.*;

@Getter
@Setter
@Entity(name = "tag")
public class TagEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "tag_seq")
    @SequenceGenerator(name = "tag_seq", sequenceName = "seq_tag", allocationSize = 1)
    @Column(name = "id_tag")
    private Integer idTag;

    @Column(name = "id_campanha", insertable = false, updatable = false)
    private Integer idCampanha;

    @Column(name = "nome_tag")
    private String nomeTag;
}

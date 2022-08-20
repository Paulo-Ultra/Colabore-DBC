package br.com.dbccompany.colaboreapi.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.Getter;
import lombok.Setter;

import javax.persistence.*;
import java.util.Set;

@Getter
@Setter
@Entity(name = "tag")
public class TagEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "tag_seq")
    @SequenceGenerator(name = "tag_seq", sequenceName = "seq_tag", allocationSize = 1)
    @Column(name = "id_tag")
    private Integer idTag;

    @Column(name = "nome_tag")
    private String nomeTag;

    @JsonIgnore
    @ManyToMany(fetch = FetchType.LAZY)
    @JoinTable(
            name = "campanha_x_tag",
            joinColumns = @JoinColumn(name = "id_tag"),
            inverseJoinColumns = @JoinColumn(name = "id_campanha")
    )
    private Set<CampanhaEntity> campanhaEntities;
}

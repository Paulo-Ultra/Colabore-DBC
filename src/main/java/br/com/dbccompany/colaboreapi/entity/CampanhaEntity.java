package br.com.dbccompany.colaboreapi.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.Getter;
import lombok.Setter;

import javax.persistence.*;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Set;

@Getter
@Setter
@Entity(name = "campanha")
public class CampanhaEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "campanha_seq")
    @SequenceGenerator(name = "campanha_seq", sequenceName = "seq_campanha", allocationSize = 1)
    @Column(name = "id_campanha")
    private Integer idCampanha;

    @Column(name = "id_usuario", insertable = false, updatable = false)
    private Integer idUsuario;

    @Column(name = "meta")
    private BigDecimal meta;

    @Column(name = "arrecadacao")
    private BigDecimal arrecadacao;

    @Column(name = "titulo")
    private String titulo;

    @Column(name = "descricao")
    private String descricao;

    @Column(name = "foto_capa")
    private String fotoCampanha;

    @Column(name = "encerrar_automaticamente")
    private Boolean encerrarAutomaticamente;

    @Column(name = "status_meta")
    private Boolean statusMeta;

    @Column(name = "data_limite")
    private LocalDateTime dataLimite;

    @Column(name = "ultima_alteracao")
    private LocalDateTime ultimaAlteracao;

    @JsonIgnore
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "id_usuario", referencedColumnName = "id_usuario")
    private UsuarioEntity usuario;

    @JsonIgnore
    @ManyToMany(fetch = FetchType.LAZY)
    @JoinTable(
            name = "campanha_x_tag",
            joinColumns = @JoinColumn(name = "id_campanha"),
            inverseJoinColumns = @JoinColumn(name = "id_tag")
    )
    private Set<TagEntity> tagEntities;

    @JsonIgnore
    @ManyToMany(fetch = FetchType.LAZY)
    @JoinTable(
            name = "campanha_x_doador",
            joinColumns = @JoinColumn(name = "id_campanha"),
            inverseJoinColumns = @JoinColumn(name = "id_doador")
    )
    private Set<DoadorEntity> doadores;
}

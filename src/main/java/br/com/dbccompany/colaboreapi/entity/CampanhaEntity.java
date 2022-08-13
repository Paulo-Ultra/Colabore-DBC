package br.com.dbccompany.colaboreapi.entity;

import lombok.Getter;
import lombok.Setter;

import javax.persistence.*;
import java.math.BigDecimal;
import java.time.LocalDateTime;

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

    @Column(name = "foto")
    private String fotoCampanha;

    @Column(name = "status_meta")
    private Boolean statusMeta;

    @Column(name = "situacao")
    private Boolean situacao;

    @Column(name = "ultima_alteracao")
    private LocalDateTime ultimaAlteracao;
}

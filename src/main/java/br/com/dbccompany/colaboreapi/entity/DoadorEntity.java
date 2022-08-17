package br.com.dbccompany.colaboreapi.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.Getter;
import lombok.Setter;

import javax.persistence.*;
import java.math.BigDecimal;
import java.util.List;
import java.util.Set;

@Getter
@Setter
@Entity(name = "doador")
public class DoadorEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "doador_seq")
    @SequenceGenerator(name = "doador_seq", sequenceName = "seq_doador", allocationSize = 1)
    @Column(name = "id_doador")
    private Integer idDoador;

    @Column(name = "id_usuario")
    private Integer idUsuario;

    @Column(name = "valor")
    private BigDecimal valor;

    @JsonIgnore
    @ManyToMany(fetch = FetchType.LAZY)
    @JoinTable(
            name = "campanha_x_doador",
            joinColumns = @JoinColumn(name = "id_doador"),
            inverseJoinColumns = @JoinColumn(name = "id_campanha")
    )
    private Set<CampanhaEntity> campanhas;

    /*@JsonIgnore
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "id_usuario", referencedColumnName = "id_usuario")
    private UsuarioEntity usuario;*/

    /*@JsonIgnore
    @ManyToMany(fetch = FetchType.LAZY)
    @JoinTable(name = "campanha_x_doador",
            joinColumns = @JoinColumn(name = "id_doador"),
            inverseJoinColumns = @JoinColumn(name = "id_usuario"))
    private List<UsuarioEntity> usuario;*/
}

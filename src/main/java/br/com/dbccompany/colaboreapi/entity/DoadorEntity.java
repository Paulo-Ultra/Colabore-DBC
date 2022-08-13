package br.com.dbccompany.colaboreapi.entity;

import lombok.Getter;
import lombok.Setter;

import javax.persistence.*;
import java.math.BigDecimal;

@Getter
@Setter
@Entity(name = "doador")
public class DoadorEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "doador_seq")
    @SequenceGenerator(name = "doador_seq", sequenceName = "seq_doador", allocationSize = 1)
    @Column(name = "id_doador")
    private Integer idDoador;

    @Column(name = "id_usuario", insertable = false, updatable = false)
    private Integer idUsuario;

    @Column(name = "valor")
    private BigDecimal valor;
}

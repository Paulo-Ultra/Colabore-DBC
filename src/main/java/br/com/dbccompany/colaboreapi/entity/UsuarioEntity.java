package br.com.dbccompany.colaboreapi.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.Getter;
import lombok.Setter;
import org.springframework.web.multipart.MultipartFile;

import javax.persistence.*;
import java.util.Set;

@Getter
@Setter
@Entity(name = "usuario")
public class UsuarioEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "usuario_seq")
    @SequenceGenerator(name = "usuario_seq", sequenceName = "seq_usuario", allocationSize = 1)
    @Column(name = "id_usuario")
    private Integer idUsuario;

    @Column(name = "nome")
    private String nome;

    @Column(name = "foto")
    private String foto;

    @JsonIgnore
    @OneToMany(fetch = FetchType.LAZY,
            mappedBy = "usuario",
            cascade = CascadeType.ALL,
            orphanRemoval = true)
    private Set<CampanhaEntity> campanha;

   /* @JsonIgnore
    @OneToOne(fetch = FetchType.LAZY, cascade = CascadeType.ALL, orphanRemoval = true)
    @JoinColumn(name = "id_doador", referencedColumnName = "id_doador")
    private DoadorEntity doador;*/
}

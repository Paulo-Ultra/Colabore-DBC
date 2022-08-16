//package br.com.dbccompany.colaboreapi.entity;
//
//import com.fasterxml.jackson.annotation.JsonIgnore;
//import lombok.Getter;
//import lombok.Setter;
//import org.springframework.security.core.GrantedAuthority;
//import org.springframework.security.core.userdetails.UserDetails;
//
//import javax.persistence.*;
//import javax.validation.constraints.NotEmpty;
//
//import java.util.Collection;
//
//@Getter
//@Setter
//@Entity(name = "autenticacao")
//public class AutenticacaoEntity {
//
//    @Id
//    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "autenticacao_seq")
//    @SequenceGenerator(name = "autenticacao_seq", sequenceName = "seq_autenticacao", allocationSize = 1)
//    @Column(name = "id_autenticacao")
//    private Integer idAutenticacao;
//
//    @Column(name = "id_usuario", insertable = false, updatable = false)
//    private Integer idUsuario;
//
//    @Column(name = "email")
//    @NotEmpty
//    private String email;
//
//    @Column(name = "senha")
//    @NotEmpty
//    private String senha;
//
//    @JsonIgnore
//    @OneToOne(fetch = FetchType.LAZY)
//    @JoinColumn(name = "id_usuario", referencedColumnName = "id_usuario")
//    private UsuarioEntity usuarioEntity;



package br.com.dbccompany.colaboreapi.dto.usuario;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import javax.validation.constraints.NotEmpty;

@Data
public class UsuarioSemSenhaDTO {

    private Integer idUsuario;

    @Schema(description = "Nome do usuário")
    private String nome;

    @NotEmpty
    private String email;

    private String foto;
}

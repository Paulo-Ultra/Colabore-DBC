package br.com.dbccompany.colaboreapi.dto.usuario;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import javax.validation.constraints.NotEmpty;

@Data
public class UsuarioSemSenhaDTO {

    @Schema(description = "Identificador do usuário.")
    private Integer idUsuario;

    @Schema(description = "Nome do usuário.")
    private String nome;

    @Schema(description = "Email do usuário.")
    @NotEmpty
    private String email;

    @Schema(description = "Foto do usuário.")
    private String foto;
}

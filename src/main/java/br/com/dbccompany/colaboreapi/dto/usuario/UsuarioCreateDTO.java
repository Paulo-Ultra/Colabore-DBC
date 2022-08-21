package br.com.dbccompany.colaboreapi.dto.usuario;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import javax.validation.constraints.NotEmpty;

@Data
public class UsuarioCreateDTO {

    @Schema(description = "Nome do usuário", example = "João")
    @NotEmpty
    private String nome;

    @Schema(description = "Email do usuário", example = "joao@dbccompany.com.br")
    @NotEmpty
    private String email;

    @Schema(description = "Senha do usuário")
    @NotEmpty
    private String senha;
}

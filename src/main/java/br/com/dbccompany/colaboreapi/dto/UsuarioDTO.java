package br.com.dbccompany.colaboreapi.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import javax.validation.constraints.NotNull;

@Data
public class UsuarioDTO extends UsuarioCreateDTO{

    @Schema(description = "Id do Usuário")
    @NotNull
    private Integer idUsuario;
}

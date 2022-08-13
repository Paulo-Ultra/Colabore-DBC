package br.com.dbccompany.colaboreapi.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

@Data
public class UsuarioDTO extends UsuarioCreateDTO {

    @Schema(description = "Identificador do usuário")
    private Integer idUsuario;
}

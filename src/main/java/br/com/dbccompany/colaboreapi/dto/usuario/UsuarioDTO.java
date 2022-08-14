package br.com.dbccompany.colaboreapi.dto.usuario;

import br.com.dbccompany.colaboreapi.dto.usuario.UsuarioCreateDTO;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

@Data
public class UsuarioDTO extends UsuarioCreateDTO {

    @Schema(description = "Identificador do usuário")
    private Integer idUsuario;
}

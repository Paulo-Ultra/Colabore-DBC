package br.com.dbccompany.colaboreapi.dto.campanha;

import br.com.dbccompany.colaboreapi.dto.doador.DoadorDTO;
import br.com.dbccompany.colaboreapi.dto.usuario.UsuarioDTO;
import br.com.dbccompany.colaboreapi.dto.usuario.UsuarioSemSenhaDTO;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.util.List;

@Data
public class CampanhaDTO extends CampanhaCreateDTO {

    @Schema(description = "Identificador do usuário")
    private Integer idCampanha;

    private List<DoadorDTO> doacoes;

    private UsuarioSemSenhaDTO usuario;

}

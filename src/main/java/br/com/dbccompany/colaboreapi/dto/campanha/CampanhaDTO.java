package br.com.dbccompany.colaboreapi.dto.campanha;

import br.com.dbccompany.colaboreapi.dto.campanha.CampanhaCreateDTO;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

@Data
public class CampanhaDTO extends CampanhaCreateDTO {

    @Schema(description = "Identificador do usuário")
    private Integer idCampanha;
}

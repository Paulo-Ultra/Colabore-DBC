package br.com.dbccompany.colaboreapi.dto.campanha;

import br.com.dbccompany.colaboreapi.dto.tag.TagDTO;
import br.com.dbccompany.colaboreapi.entity.TagEntity;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.util.List;
import java.util.Set;

@Data
public class CampanhaDTO extends CampanhaCreateDTO {

    @Schema(description = "Identificador do usuário", hidden = true)
    private Integer idCampanha;

    @Schema(hidden = true)
    private List<DoadorCampanhaDTO> doacoes;

    private Set<TagDTO> tags;
}

package br.com.dbccompany.colaboreapi.dto.tag;

import br.com.dbccompany.colaboreapi.dto.tag.TagCreateDTO;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

@Data
public class TagDTO extends TagCreateDTO {

    @Schema(description = "Identificador de tag da campanha")
    private Integer idTag;
}

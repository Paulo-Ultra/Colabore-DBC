package br.com.dbccompany.colaboreapi.dto.tag;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

@Data
public class TagCreateDTO {

    /*@Schema(hidden = true)
    private Integer idCampanha;*/

    private String nomeTag;
}

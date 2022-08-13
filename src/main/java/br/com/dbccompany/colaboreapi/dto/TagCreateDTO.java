package br.com.dbccompany.colaboreapi.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

@Data
public class TagCreateDTO {
    @Schema(name = "Identificador da campanha")
    private Integer idCampanha;

    @Schema(name = "Nome da tag da campanha")
    private String nomeTag;
}

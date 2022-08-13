package br.com.dbccompany.colaboreapi.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

@Data
public class DoadorDTO extends DoadorCreateDTO{
    @Schema(description = "Identificador do doador")
    private Integer idDoador;
}

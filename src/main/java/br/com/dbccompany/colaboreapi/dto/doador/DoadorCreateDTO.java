package br.com.dbccompany.colaboreapi.dto.doador;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.math.BigDecimal;

@Data
public class DoadorCreateDTO {

    @Schema(description = "Identificador do usuário", hidden = true)
    private Integer idUsuario;

    @Schema(description = "Valor total doado")
    private BigDecimal valor;

}

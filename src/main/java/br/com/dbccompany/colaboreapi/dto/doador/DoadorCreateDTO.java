package br.com.dbccompany.colaboreapi.dto.doador;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import javax.validation.constraints.NotNull;
import java.math.BigDecimal;

@Data
public class DoadorCreateDTO {

    @Schema(description = "Identificador do usuário.", hidden = true)
    private Integer idUsuario;

    @NotNull
    @Schema(description = "Valor total doado.", example = "500")
    private BigDecimal valor;

}

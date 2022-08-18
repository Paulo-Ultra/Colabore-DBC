package br.com.dbccompany.colaboreapi.dto.campanha;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.math.BigDecimal;

@Data
public class DoadorCampanhaDTO {

    @Schema(description = "Identificador da doação")
    private Integer idDoador;

    @Schema(description = "Identificador do usuário", hidden = true)
    private Integer idUsuario;

    @Schema(description = "Valor total doado")
    private BigDecimal valor;

    @Schema(description = "Nome do usuário")
    private String nome;

    private String foto;

}

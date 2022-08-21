package br.com.dbccompany.colaboreapi.dto.campanha;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import javax.validation.constraints.NotNull;
import java.math.BigDecimal;

@Data
public class DoadorCampanhaDTO {

    @Schema(description = "Identificador da doação.")
    private Integer idDoador;

    @Schema(description = "Identificador do usuário.", hidden = true)
    private Integer idUsuario;

    @Schema(description = "Valor total doado.")
    @NotNull
    private BigDecimal valor;

    @Schema(description = "Nome do usuário.")
    @NotNull
    private String nome;

    @Schema(description = "Foto do doador.")
    private String foto;

}

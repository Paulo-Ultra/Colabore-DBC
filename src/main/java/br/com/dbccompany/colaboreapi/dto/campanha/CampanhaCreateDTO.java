package br.com.dbccompany.colaboreapi.dto.campanha;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import javax.validation.constraints.NotNull;
import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
public class CampanhaCreateDTO {

    @Schema(hidden = true)
    private Integer idUsuario;

    @Schema(description = "Meta de arrecadação da campanha")
    @NotNull
    private BigDecimal meta;

    @Schema(description = "Arrecadação da campanha", hidden = true)
    private BigDecimal arrecadacao;

    @Schema(description = "Título da campanha")
    @NotNull
    private String titulo;

    @Schema(description = "Descrição da campanha")
    @NotNull
    private String descricao;

    @Schema(description = "Status de conclusão ou não da meta")
    @NotNull
    private Boolean encerrarAutomaticamente;

    @Schema(description = "Situação de atingimento de arrecadação da campanha", hidden = true)
    private Boolean statusMeta;

    @Schema(description = "Data em que se encerra a campanha")
    @NotNull
    private LocalDateTime dataLimite;

    @Schema(description = "Data e hora da última arrecadação da campanha", hidden = true)
    private LocalDateTime ultimaAlteracao;

    @Schema(description = "Capa da campanha", hidden = true)
    private String fotoCampanha;
}

package br.com.dbccompany.colaboreapi.dto.campanha;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
public class CampanhaCreateDTO {

    @Schema(hidden = true)
    private Integer idUsuario;

    @Schema(description = "Meta de arrecadação da campanha")
    private BigDecimal meta;

    @Schema(description = "Arrecadação da campanha", hidden = true)
    private BigDecimal arrecadacao;

    @Schema(description = "Título da campanha")
    private String titulo;

    @Schema(description = "Descrição da campanha")
    private String descricao;

    @Schema(description = "Status de conclusão ou não da meta")
    private Boolean statusMeta;

    @Schema(description = "Situação de atingimento de arrecadação da campanha", hidden = true)
    private Boolean situacao;

    private LocalDateTime dataLimite;

    @Schema(description = "Data e hora da última arrecadação da campanha", hidden = true)
    private LocalDateTime ultimaAlteracao;

    private String fotoCampanha;
}

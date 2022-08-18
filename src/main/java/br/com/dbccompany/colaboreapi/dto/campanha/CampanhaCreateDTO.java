package br.com.dbccompany.colaboreapi.dto.campanha;

import br.com.dbccompany.colaboreapi.entity.DoadorEntity;
import br.com.dbccompany.colaboreapi.entity.TagEntity;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Set;

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
    private Boolean encerrarAutomaticamente;

    @Schema(description = "Situação de atingimento de arrecadação da campanha", hidden = true)
    private Boolean statusMeta;

    @Schema(description = "Data em que se encerra a campanha")
    private LocalDateTime dataLimite;

    @Schema(description = "Data e hora da última arrecadação da campanha", hidden = true)
    private LocalDateTime ultimaAlteracao;

    @Schema(description = "Capa da campanha", hidden = true)
    private String fotoCampanha;
}

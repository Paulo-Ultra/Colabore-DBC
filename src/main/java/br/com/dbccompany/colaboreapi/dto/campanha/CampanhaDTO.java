package br.com.dbccompany.colaboreapi.dto.campanha;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.util.List;
import java.util.Set;

@Data
public class CampanhaDTO extends CampanhaCreateDTO {

    @Schema(description = "Id da campanha.", hidden = true)
    private Integer idCampanha;

    @Schema(description = "Nome do usuário.", hidden = true)
    private String nome;

    @Schema(description = "Doações da campanha.", hidden = true)
    private List<DoadorCampanhaDTO> doacoes;

    @Schema(description = "Tags da campanha.")
    private Set<String> tags;
}

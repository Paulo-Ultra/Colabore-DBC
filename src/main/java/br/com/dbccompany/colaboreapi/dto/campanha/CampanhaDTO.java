package br.com.dbccompany.colaboreapi.dto.campanha;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;
import java.util.List;
import java.util.Set;

@Data
public class CampanhaDTO extends CampanhaCreateDTO {

    @Schema(description = "Identificador do usuário", hidden = true)
    private Integer idCampanha;

    @Schema(description = "Nome do usuário")
    @NotNull
    private String nome;

    @Schema(hidden = true)
    private List<DoadorCampanhaDTO> doacoes;

    private Set<String> tags;
}

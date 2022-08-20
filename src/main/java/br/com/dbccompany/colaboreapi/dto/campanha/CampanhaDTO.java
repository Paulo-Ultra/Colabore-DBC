package br.com.dbccompany.colaboreapi.dto.campanha;

import br.com.dbccompany.colaboreapi.dto.tag.TagDTO;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import javax.validation.constraints.NotEmpty;
import java.util.List;
import java.util.Set;

@Data
public class CampanhaDTO extends CampanhaCreateDTO {

    @Schema(description = "Identificador do usuário", hidden = true)
    private Integer idCampanha;

    @Schema(description = "Nome do usuário")
    private String nome;

    @Schema(hidden = true)
    private List<DoadorCampanhaDTO> doacoes;

    @NotEmpty
    private Set<String> tags;
}

package br.com.dbccompany.colaboreapi.dto.tag;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.constraints.NotEmpty;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class TagCreateDTO {

    @Schema(description = "Nome da Tag.", example = "livros")
    @NotEmpty
    private String nomeTag;
}

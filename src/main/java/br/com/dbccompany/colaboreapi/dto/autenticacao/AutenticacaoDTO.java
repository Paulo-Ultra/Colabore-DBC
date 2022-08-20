package br.com.dbccompany.colaboreapi.dto.autenticacao;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import javax.validation.constraints.NotEmpty;

@Data
public class AutenticacaoDTO {

    @Schema(example = "colabore@dbccompany.com.br")
    @NotEmpty
    private String email;

    @NotEmpty
    private String senha;

    @Schema(hidden = true)
    private Integer idAutenticacao;
}

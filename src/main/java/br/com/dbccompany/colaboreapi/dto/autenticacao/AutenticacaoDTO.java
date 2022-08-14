package br.com.dbccompany.colaboreapi.dto.autenticacao;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

@Data
public class AutenticacaoDTO {

    @Schema(example = "colabore@dbccompany.com.br")
    private String email;

    private String senha;

    @Schema(hidden = true)
    private Integer idAutenticacao;
}

package br.com.dbccompany.colaboreapi.dto.autenticacao;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import javax.validation.constraints.NotEmpty;

@Data
public class AutenticacaoDTO {

    @Schema(description = "Email do usuário.", example = "colabore@dbccompany.com.br")
    @NotEmpty
    private String email;

    @Schema(description = "Senha do usuário.")
    @NotEmpty
    private String senha;

    @Schema(description = "Id da autenticação.", example = "1", hidden = true)
    private Integer idAutenticacao;
}

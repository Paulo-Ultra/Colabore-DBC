package br.com.dbccompany.colaboreapi.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import javax.validation.constraints.Email;

@Data
public class AutenticacaoCreateDTO {

    @Schema(example = "colabore@dbccompany.com.br")
    @Email (regexp = "@dbccompany.com.br")
    private String email;

    private String senha;
}

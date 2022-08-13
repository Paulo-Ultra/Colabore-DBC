package br.com.dbccompany.colaboreapi.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotEmpty;

@Data
public class UsuarioCreateDTO {

    @NotBlank
    @Schema(description = "Nome do Usuário")
    private String nome;

    @NotEmpty
    @Schema(description = "Foto do Usuário")
    private String foto;
}

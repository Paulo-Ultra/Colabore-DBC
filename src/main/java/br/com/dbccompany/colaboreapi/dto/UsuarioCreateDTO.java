package br.com.dbccompany.colaboreapi.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

@Data
public class UsuarioCreateDTO {

    @Schema(description = "Nome do usuário")
    private String nome;

    @Schema(description = "Foto do usuário")
    private String foto;

    private AutenticacaoDTO autenticacaoDto;
}

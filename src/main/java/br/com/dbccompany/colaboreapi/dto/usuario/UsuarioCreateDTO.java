package br.com.dbccompany.colaboreapi.dto.usuario;

import br.com.dbccompany.colaboreapi.dto.autenticacao.AutenticacaoDTO;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import org.springframework.web.multipart.MultipartFile;

@Data
public class UsuarioCreateDTO {

    @Schema(description = "Nome do usuário")
    private String nome;

    @Schema(description = "Foto do usuário")
    private MultipartFile foto;

    private AutenticacaoDTO autenticacaoDto;
}

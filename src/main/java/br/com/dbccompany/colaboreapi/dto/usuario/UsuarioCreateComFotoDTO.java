package br.com.dbccompany.colaboreapi.dto.usuario;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.*;
import org.springframework.web.multipart.MultipartFile;

@Data
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class UsuarioCreateComFotoDTO {

    @Schema(description = "Foto do usuário")
    private MultipartFile foto;
}

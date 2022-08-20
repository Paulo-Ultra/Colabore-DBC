package br.com.dbccompany.colaboreapi.dto.usuario;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.*;
import org.springframework.web.multipart.MultipartFile;

import javax.validation.constraints.NotNull;

@Data
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class UsuarioCreateComFotoDTO extends UsuarioCreateDTO {

    @NotNull
    @Schema(description = "Foto do usuário")
    private MultipartFile foto;
}

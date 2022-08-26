package br.com.dbccompany.colaboreapi.dto.campanha;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.*;
import org.springframework.web.multipart.MultipartFile;

@Data
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class CampanhaCreateComFotoDTO {
    
    @Schema(description = "Foto da campanha.")
    private MultipartFile fotoCampanha;
}

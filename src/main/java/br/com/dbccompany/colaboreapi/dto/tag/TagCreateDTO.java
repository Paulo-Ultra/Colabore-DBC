package br.com.dbccompany.colaboreapi.dto.tag;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.constraints.NotEmpty;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class TagCreateDTO {

    @NotEmpty
    private String nomeTag;
}

package br.com.dbccompany.colaboreapi.controller;

import br.com.dbccompany.colaboreapi.dto.CampanhaCreateDTO;
import br.com.dbccompany.colaboreapi.dto.CampanhaDTO;
import br.com.dbccompany.colaboreapi.exceptions.RegraDeNegocioException;
import br.com.dbccompany.colaboreapi.service.CampanhaService;
import io.swagger.v3.oas.annotations.Operation;
import lombok.RequiredArgsConstructor;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/campanha")
@Validated
@RequiredArgsConstructor
public class CampanhaController {

    private final CampanhaService campanhaService;

    @Operation(summary = "realiza o cadastro de uma campanha", description = "realiza o cadastro de uma campanha no banco de dados")
    @PostMapping("/cadastrar")
    public CampanhaDTO cadastrar (@RequestBody CampanhaCreateDTO campanhaCreateDTO) throws RegraDeNegocioException {
        return campanhaService.adicionar(campanhaCreateDTO);
    }
}

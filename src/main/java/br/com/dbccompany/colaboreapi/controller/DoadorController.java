package br.com.dbccompany.colaboreapi.controller;

import br.com.dbccompany.colaboreapi.dto.doador.DoadorCreateDTO;
import br.com.dbccompany.colaboreapi.dto.doador.DoadorDTO;
import br.com.dbccompany.colaboreapi.exceptions.CampanhaException;
import br.com.dbccompany.colaboreapi.exceptions.DoacaoException;
import br.com.dbccompany.colaboreapi.exceptions.RegraDeNegocioException;
import br.com.dbccompany.colaboreapi.service.DoadorService;
import io.swagger.v3.oas.annotations.Operation;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;

@RestController
@RequestMapping("/doador")
@Validated
@RequiredArgsConstructor
public class DoadorController {

    private final DoadorService doadorService;

    @Operation(summary = "Realiza uma doação.", description = "Realiza o cadastro de uma doação dentro do banco de dados.")
    @PostMapping("/{idCampanha}")
    public ResponseEntity<DoadorDTO> adicionar(@PathVariable("idCampanha") Integer id,
                                               @RequestBody @Valid DoadorCreateDTO doadorCreateDTO) throws RegraDeNegocioException, DoacaoException, CampanhaException {
        return ResponseEntity.ok(doadorService.adicionar(id, doadorCreateDTO));
    }
}

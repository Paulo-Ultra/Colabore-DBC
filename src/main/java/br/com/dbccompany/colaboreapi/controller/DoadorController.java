package br.com.dbccompany.colaboreapi.controller;

import br.com.dbccompany.colaboreapi.dto.doador.DoadorCreateDTO;
import br.com.dbccompany.colaboreapi.dto.doador.DoadorDTO;
import br.com.dbccompany.colaboreapi.exceptions.RegraDeNegocioException;
import br.com.dbccompany.colaboreapi.service.DoadorService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.validation.Valid;

@RestController
@RequestMapping("/doador")
@Validated
@RequiredArgsConstructor
public class DoadorController {

    private final DoadorService doadorService;

    @PostMapping
    public ResponseEntity<DoadorDTO> adicionar(@Valid @RequestBody DoadorCreateDTO doadorCreateDTO) throws RegraDeNegocioException {
        return ResponseEntity.ok(doadorService.adicionar(doadorCreateDTO));
    }
}

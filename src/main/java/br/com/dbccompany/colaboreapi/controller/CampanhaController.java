package br.com.dbccompany.colaboreapi.controller;

import br.com.dbccompany.colaboreapi.dto.CampanhaCreateDTO;
import br.com.dbccompany.colaboreapi.dto.CampanhaDTO;
import br.com.dbccompany.colaboreapi.exceptions.CampanhaNaoEncontradaException;
import br.com.dbccompany.colaboreapi.exceptions.RegraDeNegocioException;
import br.com.dbccompany.colaboreapi.service.CampanhaService;
import io.swagger.v3.oas.annotations.Operation;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/campanha")
@Validated
@RequiredArgsConstructor
public class CampanhaController {

    private final CampanhaService campanhaService;

    @Operation(summary = "realiza o cadastro de uma campanha", description = "realiza o cadastro de uma campanha no banco de dados")
    @PostMapping("/cadastrar")
    public ResponseEntity<CampanhaDTO> cadastrar (@RequestBody CampanhaCreateDTO campanhaCreateDTO) throws RegraDeNegocioException {
        return new ResponseEntity<>(campanhaService.adicionar(campanhaCreateDTO), HttpStatus.CREATED);
    }

    @Operation(summary = "realiza a listagem de todas as campanhas", description = "lista de todas as campanhas no banco de dados")
    @GetMapping("/listar")
    public ResponseEntity<List<CampanhaDTO>> listarCampanhas() throws RegraDeNegocioException, CampanhaNaoEncontradaException {
        return new ResponseEntity<>(campanhaService.listaDeCampanhas(), HttpStatus.OK);
    }

    @Operation(summary = "realiza a listagem de todas as campanhas do usuário logado", description = "lista de todas as campanhas do usuário logado no banco de dados")
    @GetMapping("/listarCampanhasDoUsuario")
    public ResponseEntity<List<CampanhaDTO>> listarCampanhasDoUsuario() throws RegraDeNegocioException, CampanhaNaoEncontradaException {
        return new ResponseEntity<>(campanhaService.listaDeCampanhasByUsuarioLogado(), HttpStatus.OK);
    }

    @Operation(summary = "realiza a deleção da campanha do usuário logado", description = "delete de campanha pelo identificador no banco de dados")
    @DeleteMapping("/delete")
    public ResponseEntity<Void> deleteCampanha(@RequestParam Integer id) throws CampanhaNaoEncontradaException {
        campanhaService.deletar(id);
        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }
}

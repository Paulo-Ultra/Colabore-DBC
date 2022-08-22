package br.com.dbccompany.colaboreapi.controller;

import br.com.dbccompany.colaboreapi.dto.campanha.CampanhaDTO;
import br.com.dbccompany.colaboreapi.enums.TipoFiltro;
import br.com.dbccompany.colaboreapi.exceptions.AmazonS3Exception;
import br.com.dbccompany.colaboreapi.exceptions.CampanhaException;
import br.com.dbccompany.colaboreapi.exceptions.RegraDeNegocioException;
import br.com.dbccompany.colaboreapi.service.CampanhaService;
import io.swagger.v3.oas.annotations.Operation;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.validation.Valid;
import javax.validation.constraints.NotNull;
import java.io.IOException;
import java.util.List;

import static org.springframework.web.bind.annotation.RequestMethod.POST;


@RestController
@RequestMapping("/campanha")
@Validated
@RequiredArgsConstructor
public class CampanhaController {

    private final CampanhaService campanhaService;

    @Operation(summary = "Realiza o cadastro de uma campanha.", description = "Realiza o cadastro de uma campanha dentro do banco de dados.")
    @PostMapping("/cadastrar")
    public ResponseEntity<CampanhaDTO> cadastrar (@RequestBody @Valid CampanhaDTO campanhaCreateDTO) throws RegraDeNegocioException, CampanhaException {
        return new ResponseEntity<>(campanhaService.adicionar(campanhaCreateDTO), HttpStatus.CREATED);
    }

    @Operation(summary = "Realiza o cadastro de uma foto para campanha.", description = "Realiza o cadastro de uma foto para campanha dentro do banco de dados.")
    @RequestMapping(
            path = "/cadastrarFoto",
            method = POST,
            consumes = {MediaType.MULTIPART_FORM_DATA_VALUE}
    )
    public ResponseEntity<Void> cadastrarFoto (@ModelAttribute @Valid MultipartFile multipartFile,
                                               @RequestParam Integer idCampanha) throws AmazonS3Exception, CampanhaException, IOException {
        campanhaService.adicionarFoto(idCampanha, multipartFile);
        return ResponseEntity.ok().build();
    }

    @Operation(summary = "Realiza a listagem de todas as campanhas do usuário logado.", description = "Lista todas as campanhas presentes no banco de dados para o usuário logado.")
    @GetMapping("/listarCampanhasDoUsuario")
    public ResponseEntity<List<CampanhaDTO>> listarCampanhasDoUsuario() {
        return new ResponseEntity<>(campanhaService.listaDeCampanhasByUsuarioLogado(), HttpStatus.OK);
    }

    @Operation(summary = "Realiza a edição de uma determinada campanha.", description = "Executa a edição de uma campanha no banco de dados, através de seu respectivo id.")
    @PutMapping("/{idCampanha}")
    public ResponseEntity<CampanhaDTO> editar(@PathVariable("idCampanha") Integer idCampanha,
                                              @RequestBody @Valid CampanhaDTO campanhaDTO) throws CampanhaException, RegraDeNegocioException {
        return new ResponseEntity<>(campanhaService.editar(idCampanha, campanhaDTO), HttpStatus.OK);
    }

    @Operation(summary = "Realiza a deleção da campanha do usuário logado.", description = "Delete de campanha pelo seu identificador no banco de dados.")
    @DeleteMapping("/delete")
    public ResponseEntity<Void> deleteCampanha(@RequestParam Integer id) throws CampanhaException {
        campanhaService.deletar(id);
        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }

    @Operation(summary = "Realiza a listagem da campanha pelo id informado.", description = "Lista as informações da campanha pelo seu respectivo id.")
    @GetMapping("/campanhaPeloId")
    public ResponseEntity<CampanhaDTO> CampanhaPeloId(@RequestParam Integer idCampanha) throws CampanhaException {
        return new ResponseEntity<>(campanhaService.localizarCampanha(idCampanha), HttpStatus.OK);
    }

    @Operation(summary = "Realiza a listagem das campanhas através de determinados filtros.", description = "Recupera as campanhas presentes no banco de dados através de determinados filtros.")
    @GetMapping("/listarCampanhas")
    public ResponseEntity<List<CampanhaDTO>> listarCampanha(@RequestParam TipoFiltro tipoFiltro, @RequestParam boolean minhasContribuicoes, @RequestParam boolean minhasCampanhas, @RequestParam(required = false) List<Integer> idTags) {
        return new ResponseEntity<>(campanhaService.listarCampanha(tipoFiltro, minhasContribuicoes, minhasCampanhas, idTags), HttpStatus.OK);
    }
}

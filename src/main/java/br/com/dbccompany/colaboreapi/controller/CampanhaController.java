package br.com.dbccompany.colaboreapi.controller;

import br.com.dbccompany.colaboreapi.dto.campanha.CampanhaCreateDTO;
import br.com.dbccompany.colaboreapi.dto.campanha.CampanhaDTO;
import br.com.dbccompany.colaboreapi.enums.TipoFiltro;
import br.com.dbccompany.colaboreapi.exceptions.AmazonS3Exception;
import br.com.dbccompany.colaboreapi.exceptions.CampanhaNaoEncontradaException;
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
import java.util.List;

import static org.springframework.web.bind.annotation.RequestMethod.POST;


@RestController
@RequestMapping("/campanha")
@Validated
@RequiredArgsConstructor
public class CampanhaController {

    private final CampanhaService campanhaService;

    @PostMapping("/cadastrar")
    public ResponseEntity<CampanhaDTO> cadastrar (@Valid @RequestBody CampanhaDTO campanhaCreateDTO) throws RegraDeNegocioException, AmazonS3Exception {
        return new ResponseEntity<>(campanhaService.adicionar(campanhaCreateDTO), HttpStatus.CREATED);
    }

    @RequestMapping(
            path = "/cadastrarFoto",
            method = POST,
            consumes = {MediaType.MULTIPART_FORM_DATA_VALUE}
    )
    public ResponseEntity<Void> cadastrarFoto (@ModelAttribute MultipartFile multipartFile,
                                               @RequestParam Integer idCampanha) throws RegraDeNegocioException, AmazonS3Exception {
        campanhaService.adicionarFoto(idCampanha, multipartFile);
        return ResponseEntity.ok().build();
    }

    @Operation(summary = "realiza a listagem de todas as campanhas do usuário logado", description = "lista de todas as campanhas do usuário logado no banco de dados")
    @GetMapping("/listarCampanhasDoUsuario")
    public ResponseEntity<List<CampanhaDTO>> listarCampanhasDoUsuario() {
        return new ResponseEntity<>(campanhaService.listaDeCampanhasByUsuarioLogado(), HttpStatus.OK);
    }

    @PutMapping("/{idCampanha}")
    public ResponseEntity<CampanhaDTO> editar(@PathVariable("idCampanha") Integer idCampanha,
                                              @Valid @RequestBody CampanhaCreateDTO campanhaCreateDTO) throws CampanhaNaoEncontradaException, RegraDeNegocioException {
        return new ResponseEntity<>(campanhaService.editar(idCampanha, campanhaCreateDTO), HttpStatus.OK);
    }

    @Operation(summary = "realiza a deleção da campanha do usuário logado", description = "delete de campanha pelo identificador no banco de dados")
    @DeleteMapping("/delete")
    public ResponseEntity<Void> deleteCampanha(@RequestParam Integer id) throws CampanhaNaoEncontradaException, RegraDeNegocioException {
        campanhaService.deletar(id);
        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }

    @Operation(summary = "realiza a listagem da campanha pelo id informado", description = "lista as informações da campanha pelo id no banco de dados")
    @GetMapping("/campanhaPeloId")
    public ResponseEntity<CampanhaDTO> CampanhaPeloId(@RequestParam Integer idCampanha) throws CampanhaNaoEncontradaException {
        return new ResponseEntity<>(campanhaService.campanhaPeloId(idCampanha), HttpStatus.OK);
    }

    @Operation(summary = "realiza a listagem das campanhas com os status de atingida",
            description = "lista as informações da campanha pelo status da meta como conclúídas no banco de dados")
    @GetMapping("/listarCampanhas")
    public ResponseEntity<List<CampanhaDTO>> listarCampanha(@RequestParam TipoFiltro tipoFiltro, @RequestParam boolean minhasContribuicoes, @RequestParam boolean minhasCampanhas) throws RegraDeNegocioException, CampanhaNaoEncontradaException {
        return new ResponseEntity<>(campanhaService.listarCampanha(tipoFiltro, minhasContribuicoes, minhasCampanhas), HttpStatus.OK);
    }
}

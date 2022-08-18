package br.com.dbccompany.colaboreapi.controller;

import br.com.dbccompany.colaboreapi.dto.tag.TagCreateDTO;
import br.com.dbccompany.colaboreapi.dto.tag.TagDTO;
import br.com.dbccompany.colaboreapi.exceptions.RegraDeNegocioException;
import br.com.dbccompany.colaboreapi.service.TagService;

import io.swagger.v3.oas.annotations.Operation;
import lombok.RequiredArgsConstructor;

import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.List;

@RestController
@RequestMapping("/tag")
@Validated
@RequiredArgsConstructor
public class TagController {

    private final TagService tagService;

    @Operation(summary = "Adiciona uma tag.", description = "Adiciona uma tag dentro do banco de dados.")
    @PostMapping()
    public ResponseEntity<TagDTO> adicionar(@Valid @RequestBody TagCreateDTO tagCreateDTO) throws RegraDeNegocioException {
        return ResponseEntity.ok(tagService.adicionar(tagCreateDTO));
    }

    @Operation(summary = "Lista todas as tags cadastradas.", description = "Lista todas as tags cadastradas no banco de dados.")
    @GetMapping
    public List<TagDTO> list() {
        List<TagDTO> tagDTOS = tagService.list();
        return tagDTOS;
    }

    @Operation(summary = "Lista todas as tags da campanha.", description = "Lista todas as tags vinculadas à uma campanha, através do id da campanha.")
    @GetMapping("/{idCampanha}")
    public List<TagDTO> listTagCampanha(@PathVariable("idCampanha") Integer idCampanha) {
        List<TagDTO> tagDTOS = tagService.listTagCampanha(idCampanha);
        return tagDTOS;
    }

    @Operation(summary = "Deleta uma tag.", description = "Deleta uma tag do banco de dados através de seu id")
    @DeleteMapping("/{id}")
    public void delete(@PathVariable("id") Integer id) throws RegraDeNegocioException {
        tagService.delete(id);
    }
}

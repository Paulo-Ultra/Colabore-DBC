package br.com.dbccompany.colaboreapi.controller;

import br.com.dbccompany.colaboreapi.dto.tag.TagCreateDTO;
import br.com.dbccompany.colaboreapi.dto.tag.TagDTO;
import br.com.dbccompany.colaboreapi.entity.TagEntity;
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

    // FIXME, EXPONDO ENTITY????????????
    @Operation(summary = "Adiciona uma tag.", description = "Adiciona uma tag dentro do banco de dados.")
    @PostMapping()
    public ResponseEntity<TagEntity> adicionar(@RequestBody @Valid TagCreateDTO tagCreateDTO) throws RegraDeNegocioException {
        return ResponseEntity.ok(tagService.adicionar(tagCreateDTO));
    }

    @Operation(summary = "Lista todas as tags cadastradas.", description = "Lista todas as tags cadastradas no banco de dados.")
    @GetMapping
    public List<TagDTO> list() {
        List<TagDTO> tagDTOS = tagService.list();
        return tagDTOS;
    }

    @Operation(summary = "Deleta uma tag.", description = "Deleta uma tag no banco de dados através de seu id")
    @DeleteMapping("/{id}")
    public void delete(@PathVariable("id") Integer id) throws RegraDeNegocioException {
        tagService.delete(id);
    }
}

package br.com.dbccompany.colaboreapi.controller;

import br.com.dbccompany.colaboreapi.dto.doador.DoadorCreateDTO;
import br.com.dbccompany.colaboreapi.dto.doador.DoadorDTO;
import br.com.dbccompany.colaboreapi.dto.tag.TagCreateDTO;
import br.com.dbccompany.colaboreapi.dto.tag.TagDTO;
import br.com.dbccompany.colaboreapi.exceptions.RegraDeNegocioException;
import br.com.dbccompany.colaboreapi.service.DoadorService;
import br.com.dbccompany.colaboreapi.service.TagService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;

@RestController
@RequestMapping("/tag")
@Validated
@RequiredArgsConstructor
public class TagController {

    private final TagService tagService;

    @PostMapping("/{idCampanha}")
    public ResponseEntity<TagDTO> adicionar(@PathVariable("idCampanha") Integer id,
                                            @Valid @RequestBody TagCreateDTO tagCreateDTO) throws RegraDeNegocioException {
        return ResponseEntity.ok(tagService.adicionar(id, tagCreateDTO));
    }

}

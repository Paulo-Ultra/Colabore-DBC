package br.com.dbccompany.colaboreapi.controller;

import br.com.dbccompany.colaboreapi.dto.UsuarioDTO;
import br.com.dbccompany.colaboreapi.entity.AutenticacaoEntity;
import br.com.dbccompany.colaboreapi.exceptions.RegraDeNegocioException;
import br.com.dbccompany.colaboreapi.service.AutenticacaoService;
import br.com.dbccompany.colaboreapi.service.UsuarioService;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/usuario")
@Validated
@RequiredArgsConstructor
public class UsuarioController {

    @Autowired
    private UsuarioService usuarioService;

    @Autowired
    private AutenticacaoService autenticacaoService;

    @GetMapping("/listar")
    public List<UsuarioDTO> listar() throws RegraDeNegocioException {
        return usuarioService.listar();
    }
}

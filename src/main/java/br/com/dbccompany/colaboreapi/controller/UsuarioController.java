package br.com.dbccompany.colaboreapi.controller;

import br.com.dbccompany.colaboreapi.dto.usuario.UsuarioSemSenhaDTO;
import br.com.dbccompany.colaboreapi.exceptions.RegraDeNegocioException;
import br.com.dbccompany.colaboreapi.service.UsuarioService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/usuario")
@Validated
@RequiredArgsConstructor
public class UsuarioController {

    private final UsuarioService usuarioService;

    @GetMapping("/dadosUsuario")
    public ResponseEntity<UsuarioSemSenhaDTO> findLoggedUser() throws RegraDeNegocioException {
        return new ResponseEntity<>(usuarioService.retornarUsuarioLogado(), HttpStatus.OK);
    }
}

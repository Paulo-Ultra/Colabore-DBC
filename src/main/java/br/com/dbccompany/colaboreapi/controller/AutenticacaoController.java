package br.com.dbccompany.colaboreapi.controller;

import br.com.dbccompany.colaboreapi.dto.autenticacao.AutenticacaoDTO;
import br.com.dbccompany.colaboreapi.dto.usuario.UsuarioCreateComFotoDTO;
import br.com.dbccompany.colaboreapi.dto.usuario.UsuarioCreateDTO;
import br.com.dbccompany.colaboreapi.dto.usuario.UsuarioDTO;
import br.com.dbccompany.colaboreapi.entity.AutenticacaoEntity;
import br.com.dbccompany.colaboreapi.exceptions.AmazonS3Exception;
import br.com.dbccompany.colaboreapi.exceptions.RegraDeNegocioException;
import br.com.dbccompany.colaboreapi.security.TokenService;
import br.com.dbccompany.colaboreapi.service.UsuarioService;
import io.swagger.v3.oas.annotations.Operation;
import lombok.RequiredArgsConstructor;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;

import java.awt.*;

import static org.springframework.web.bind.annotation.RequestMethod.POST;

@RestController
@RequestMapping("/autenticacao")
@Validated
@RequiredArgsConstructor
public class AutenticacaoController {

    private final TokenService tokenService;

    private final AuthenticationManager authenticationManager;

    private final UsuarioService usuarioService;

    @Operation(summary = "Realiza o login de um usuário", description = "Realiza o login de um determinado usuário gerando seu respectivo token")
    @PostMapping("/login")
    public String auth(@RequestBody @Valid AutenticacaoDTO autenticacaoCreateDto) throws RegraDeNegocioException {

        UsernamePasswordAuthenticationToken usernamePasswordAuthenticationToken =
                new UsernamePasswordAuthenticationToken(
                        autenticacaoCreateDto.getEmail(),
                        autenticacaoCreateDto.getSenha()
                );

        Authentication authentication = authenticationManager.authenticate(usernamePasswordAuthenticationToken);

        Object usuarioLogado = authentication.getPrincipal();
        AutenticacaoEntity autenticacaoEntity = (AutenticacaoEntity) usuarioLogado;

        String token = tokenService.getToken(autenticacaoEntity);

        return token;
    }

    @RequestMapping(
            path = "/cadastrar",
            method = POST,
            consumes = {MediaType.MULTIPART_FORM_DATA_VALUE}
    )
    public ResponseEntity<UsuarioDTO> criarUsuario(@Valid @ModelAttribute UsuarioCreateComFotoDTO usuarioCreateDTO) throws AmazonS3Exception, RegraDeNegocioException {
        return ResponseEntity.ok(usuarioService.adicionar(usuarioCreateDTO));
    }
}

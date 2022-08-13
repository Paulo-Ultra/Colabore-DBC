package br.com.dbccompany.colaboreapi.controller;

import br.com.dbccompany.colaboreapi.dto.AutenticacaoDTO;
import br.com.dbccompany.colaboreapi.dto.UsuarioCreateDTO;
import br.com.dbccompany.colaboreapi.dto.UsuarioDTO;
import br.com.dbccompany.colaboreapi.entity.AutenticacaoEntity;
import br.com.dbccompany.colaboreapi.exceptions.RegraDeNegocioException;
import br.com.dbccompany.colaboreapi.security.TokenService;
import br.com.dbccompany.colaboreapi.service.UsuarioService;
import io.swagger.v3.oas.annotations.Operation;
import lombok.RequiredArgsConstructor;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;

@RestController
@RequestMapping("/autenticacao")
@Validated
@RequiredArgsConstructor
public class AutenticacaoController {

    private final TokenService tokenService;

    private final AuthenticationManager authenticationManager;

    @Autowired
    private UsuarioService usuarioService;

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

    @Operation(summary = "realiza o registro de um usuário", description = "realiza o registro de um usuario, criptografando sua senha no banco de dados")
    @PostMapping("/registrar")
    public UsuarioDTO cadastrar (@RequestBody UsuarioCreateDTO usuarioCreateDto) throws RegraDeNegocioException {
        return usuarioService.adicionar(usuarioCreateDto);
    }
}

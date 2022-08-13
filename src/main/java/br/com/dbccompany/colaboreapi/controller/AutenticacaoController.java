package br.com.dbccompany.colaboreapi.controller;

import br.com.dbccompany.colaboreapi.dto.AutenticacaoCreateDto;
import br.com.dbccompany.colaboreapi.dto.AutenticacaoDto;
import br.com.dbccompany.colaboreapi.entity.UsuarioEntity;
import br.com.dbccompany.colaboreapi.exceptions.RegraDeNegocioException;
import br.com.dbccompany.colaboreapi.security.TokenService;
import io.swagger.v3.oas.annotations.Operation;
import lombok.RequiredArgsConstructor;

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

/*    @Autowired
    private UsuarioService usuarioService;*/

    @Operation(summary = "Realiza o login de um usuário", description = "Realiza o login de um determinado usuário gerando seu respectivo token")
    @PostMapping("/login")
    public String auth(@RequestBody @Valid AutenticacaoCreateDto autenticacaoDto) throws RegraDeNegocioException {

        UsernamePasswordAuthenticationToken usernamePasswordAuthenticationToken =
                new UsernamePasswordAuthenticationToken(
                        autenticacaoDto.getEmail(),
                        autenticacaoDto.getSenha()
                );

        Authentication authentication = authenticationManager.authenticate(usernamePasswordAuthenticationToken);

        Object usuarioLogado = authentication.getPrincipal();
        UsuarioEntity usuarioEntity = (UsuarioEntity) usuarioLogado;

        String token = tokenService.getToken(usuarioEntity);

        return token;
    }

    /*@Operation(summary = "realiza o registro de um usuário", description = "realiza o registro de um usuario, criptografando sua senha no banco de dados")
    @PostMapping("/registrar")
    public UsuarioDTO cadastrar (@RequestBody UsuarioCreateDTO usuarioCreateDTO){
        return usuarioService.registrar(usuarioCreateDTO);
    }*/
}

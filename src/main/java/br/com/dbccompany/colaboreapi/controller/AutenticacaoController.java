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
import org.springframework.http.HttpStatus;
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
import static org.springframework.web.bind.annotation.RequestMethod.PUT;

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

    @Operation(summary = "realiza a listagem de todas as campanhas", description = "lista de todas as campanhas no banco de dados")
    @PostMapping("/cadastrar")
    public String criarUsuario(@Valid @RequestBody UsuarioCreateDTO usuarioCreateDTO) throws RegraDeNegocioException {
//        return ResponseEntity.ok(usuarioService.adicionar(usuarioCreateDTO));

        usuarioService.adicionar(usuarioCreateDTO);

        UsernamePasswordAuthenticationToken usernamePasswordAuthenticationToken =
                new UsernamePasswordAuthenticationToken(
                        usuarioCreateDTO.getEmail(),
                        usuarioCreateDTO.getSenha()
                );

        Authentication authentication = authenticationManager.authenticate(usernamePasswordAuthenticationToken);

        Object usuarioLogado = authentication.getPrincipal();
        AutenticacaoEntity autenticacaoEntity = (AutenticacaoEntity) usuarioLogado;

        String token = tokenService.getToken(autenticacaoEntity);

        return token;
    }

    @RequestMapping(
            path = "/cadastrarFoto",
            method = PUT,
            consumes = {MediaType.MULTIPART_FORM_DATA_VALUE}
    )
    public ResponseEntity<UsuarioCreateComFotoDTO> criarFotoUsuario(@Valid @ModelAttribute UsuarioCreateComFotoDTO usuarioCreateComFotoDTO) throws RegraDeNegocioException, AmazonS3Exception {
        return new ResponseEntity (usuarioService.adicionarFoto(usuarioCreateComFotoDTO), HttpStatus.ACCEPTED);
    }
}

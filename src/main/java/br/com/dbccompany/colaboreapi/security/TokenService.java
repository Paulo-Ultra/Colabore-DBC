package br.com.dbccompany.colaboreapi.security;

import br.com.dbccompany.colaboreapi.entity.UsuarioEntity;
import br.com.dbccompany.colaboreapi.exceptions.RegraDeNegocioException;
import br.com.dbccompany.colaboreapi.service.UsuarioService;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.stereotype.Service;

import java.util.Collections;
import java.util.Date;

@Service
@RequiredArgsConstructor
public class TokenService {

    @Value("${jwt.secret}")
    private String secret;

    @Value("${jwt.expiration}")
    private String expiration;

    private final UsuarioService usuarioService;
    //private static final String TOKEN_PREFIX = "Bearer ";


    //criando um token JWT
    public String getToken(UsuarioEntity usuarioEntity) throws RegraDeNegocioException {

        Date now = new Date();
        Date exp = new Date(now.getTime() + Long.valueOf(expiration)); //convertendo para long

        String token = Jwts.builder()
                .setIssuer("colabore-api")
                .claim(Claims.ID, usuarioEntity.getIdUsuario())
                .setIssuedAt(now)
                .setExpiration(exp)
                .signWith(SignatureAlgorithm.HS256, secret)
                .compact();

        //return TOKEN_PREFIX + token;
        return TokenAuthenticationFilter.BEARER + token;
    }

    //validar se o token é válido e retornar o usuário se for válido
    public UsernamePasswordAuthenticationToken isValid(String token) {
        if(token == null) {
            return null;
        }

        Claims body = Jwts.parser()
                .setSigningKey(secret)
                .parseClaimsJws(token)
                .getBody();

        Integer idAutenticacao = body.get(Claims.ID, Integer.class);

        if (idAutenticacao != null) {
            UsernamePasswordAuthenticationToken usernamePasswordAuthenticationToken =
                    new UsernamePasswordAuthenticationToken(idAutenticacao, null, Collections.emptyList());

            return usernamePasswordAuthenticationToken;
        }
        return null;
    }
}
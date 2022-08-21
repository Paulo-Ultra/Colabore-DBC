package br.com.dbccompany.colaboreapi.security;

import br.com.dbccompany.colaboreapi.entity.UsuarioEntity;
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

    public String getToken(UsuarioEntity usuarioEntity) {

        Date now = new Date();
        Date exp = new Date(now.getTime() + Long.valueOf(expiration));

        String token = Jwts.builder()
                .setIssuer("colabore-api")
                .claim(Claims.ID, usuarioEntity.getIdUsuario())
                .setIssuedAt(now)
                .setExpiration(exp)
                .signWith(SignatureAlgorithm.HS256, secret)
                .compact();

        return TokenAuthenticationFilter.BEARER + token;
    }

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
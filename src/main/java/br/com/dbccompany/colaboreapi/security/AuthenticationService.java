package br.com.dbccompany.colaboreapi.security;

import br.com.dbccompany.colaboreapi.entity.AutenticacaoEntity;
import br.com.dbccompany.colaboreapi.service.AutenticacaoService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
@RequiredArgsConstructor
public class AuthenticationService implements UserDetailsService {

    private final AutenticacaoService autenticacaoService;

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        //buscar usuário pelo login
        Optional<AutenticacaoEntity> optionalAutenticacaoEntity = autenticacaoService.findByEmail(username);

        return optionalAutenticacaoEntity
                .orElseThrow(() -> new UsernameNotFoundException("Usuário Inválido"));

    }

   /* @Override //2:11
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {

        //buscar usuário pelo login
        Optional<AutenticacaoEntity> optionalAutenticacaoEntity = usuarioService.findByLogin(username);

        return optionalAutenticacaoEntity
                .orElseThrow(() -> new UsernameNotFoundException("Usuário Inválido"));

    }*/
}
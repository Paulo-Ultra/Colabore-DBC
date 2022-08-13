package br.com.dbccompany.colaboreapi.service;

import br.com.dbccompany.colaboreapi.entity.AutenticacaoEntity;
import br.com.dbccompany.colaboreapi.exceptions.RegraDeNegocioException;
import br.com.dbccompany.colaboreapi.repository.AutenticacaoRepository;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.AllArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
@AllArgsConstructor
public class AutenticacaoService {

    private final ObjectMapper objectMapper;
    @Autowired
    private AutenticacaoRepository autenticacaoRepository;

    public String criptografarSenha(String senha) {

        BCryptPasswordEncoder bCryptPasswordEncoder = new BCryptPasswordEncoder();

        String criptografia = bCryptPasswordEncoder.encode(senha);

        return criptografia;
    }

    /*public Optional<AutenticacaoEntity> findByLoginAndSenha(String login, String senha) {
        return autenticacaoRepository.findByLoginAndSenha(login, senha);
    }*/


    public Optional<AutenticacaoEntity> findByEmail(String email) {
        return autenticacaoRepository.findByEmail(email);
    }


    public Integer getIdLoggedUser() {
        Integer findUserId = (Integer) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        return findUserId;
    }

    public AutenticacaoEntity getLoggedUser() throws RegraDeNegocioException {
        return this.findById(getIdLoggedUser());
    }

    public AutenticacaoEntity findById(Integer idAutenticacao) throws RegraDeNegocioException {
        return autenticacaoRepository.findById(idAutenticacao)
                .orElseThrow((() -> new RegraDeNegocioException("Usuario nao encontrado")));
    }


    public AutenticacaoEntity findByIdUsuario(Integer idUsuario) throws RegraDeNegocioException {
        AutenticacaoEntity userLoginRecuperado = autenticacaoRepository.findAll().stream()
                //.filter(usuario -> usuario.getIdUsuario().equals(idUsuario))
                .findFirst()
                .orElseThrow(() -> new RegraDeNegocioException("Usuário não encontrado"));
        return userLoginRecuperado;
    }
}

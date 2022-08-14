package br.com.dbccompany.colaboreapi.service;

import br.com.dbccompany.colaboreapi.dto.usuario.UsuarioCreateDTO;
import br.com.dbccompany.colaboreapi.dto.usuario.UsuarioDTO;
import br.com.dbccompany.colaboreapi.entity.AutenticacaoEntity;
import br.com.dbccompany.colaboreapi.entity.UsuarioEntity;

import br.com.dbccompany.colaboreapi.exceptions.RegraDeNegocioException;
import br.com.dbccompany.colaboreapi.repository.AutenticacaoRepository;
import br.com.dbccompany.colaboreapi.repository.UsuarioRepository;

import com.fasterxml.jackson.databind.ObjectMapper;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;

import org.springframework.stereotype.Service;

import java.util.List;

import java.util.stream.Collectors;

@Slf4j
@Service
public class UsuarioService {

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private UsuarioRepository usuarioRepository;

    @Autowired
    private AutenticacaoRepository autenticacaoRepository;

    @Autowired
    private AutenticacaoService autenticacaoService;

    public UsuarioDTO adicionar(UsuarioCreateDTO usuarioCreateDto) throws RegraDeNegocioException {

        validarEmail(usuarioCreateDto.getAutenticacaoDto().getEmail());

        UsuarioEntity usuarioEntity = objectMapper.convertValue(usuarioCreateDto, UsuarioEntity.class);
        UsuarioEntity usuario = usuarioRepository.save(usuarioEntity);

        AutenticacaoEntity autenticacaoEntity = objectMapper.convertValue(usuarioCreateDto.getAutenticacaoDto(), AutenticacaoEntity.class);

        autenticacaoEntity.setUsuarioEntity(usuario);
        autenticacaoEntity.setSenha(autenticacaoService.criptografarSenha(autenticacaoEntity.getSenha()));
        autenticacaoRepository.save(autenticacaoEntity);

        return objectMapper.convertValue(usuario, UsuarioDTO.class);
    }

    public List<UsuarioDTO> listar() throws RegraDeNegocioException {
        Integer idLoggedUser = autenticacaoService.getIdLoggedUser();
        AutenticacaoEntity usuarioLogadoEntity = autenticacaoService.findById(idLoggedUser);

        Integer id = (Integer) usuarioLogadoEntity.getIdUsuario();

        localizarUsuario(id);
        return usuarioRepository.findById(id).stream()
                .filter(usuario -> usuario.getIdUsuario().equals(id))
                .map(usuario -> objectMapper.convertValue(usuario, UsuarioDTO.class))
                .collect(Collectors.toList());
    }

    public void validarEmail(String emailParaValidar) throws RegraDeNegocioException {
        if (emailParaValidar.matches("^(.+)@dbccompany.com.br")) {
            log.info("email validado");
        } else {
            throw new RegraDeNegocioException("Padrão de e-mail incorreto");
        }
    }

    public UsuarioEntity localizarUsuario(Integer idUsuario) throws RegraDeNegocioException {
        UsuarioEntity usuarioRecuperado = usuarioRepository.findAll().stream()
                .filter(usuario -> usuario.getIdUsuario().equals(idUsuario))
                .findFirst()
                .orElseThrow(() -> new RegraDeNegocioException("Usuário não encontrado"));
        return usuarioRecuperado;
    }

    public Integer idUsuarioLogado() throws RegraDeNegocioException {
        Integer idLoggedUser = autenticacaoService.getIdLoggedUser();
        AutenticacaoEntity usuarioLogadoEntity = autenticacaoService.findById(idLoggedUser);

        Integer id = (Integer) usuarioLogadoEntity.getIdUsuario();
        return id;
    }
}
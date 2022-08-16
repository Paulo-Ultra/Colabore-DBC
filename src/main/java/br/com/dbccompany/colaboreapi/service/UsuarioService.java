package br.com.dbccompany.colaboreapi.service;

import br.com.dbccompany.colaboreapi.dto.usuario.UsuarioCreateComFotoDTO;
import br.com.dbccompany.colaboreapi.dto.usuario.UsuarioCreateDTO;
import br.com.dbccompany.colaboreapi.dto.usuario.UsuarioDTO;
import br.com.dbccompany.colaboreapi.entity.AutenticacaoEntity;
import br.com.dbccompany.colaboreapi.entity.UsuarioEntity;

import br.com.dbccompany.colaboreapi.exceptions.AmazonS3Exception;
import br.com.dbccompany.colaboreapi.exceptions.RegraDeNegocioException;
import br.com.dbccompany.colaboreapi.repository.AutenticacaoRepository;
import br.com.dbccompany.colaboreapi.repository.UsuarioRepository;

import com.fasterxml.jackson.databind.ObjectMapper;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;

import org.springframework.stereotype.Service;

import java.net.URI;
import java.util.List;

import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
public class UsuarioService {

    private final ObjectMapper objectMapper;

    private final UsuarioRepository usuarioRepository;

    private final AutenticacaoRepository autenticacaoRepository;

    private final AutenticacaoService autenticacaoService;

    private final S3Service s3Service;

    public UsuarioDTO adicionarFoto(UsuarioCreateComFotoDTO usuarioCreateComFotoDTO) throws RegraDeNegocioException, AmazonS3Exception {

        UsuarioEntity usuarioEntity = localizarUsuario(idUsuarioLogado());
        URI uri = s3Service.uploadFile(usuarioCreateComFotoDTO.getFoto());
        usuarioEntity.setFoto(uri.toString());

        UsuarioEntity usuario = usuarioRepository.save(usuarioEntity);
        return objectMapper.convertValue(usuario, UsuarioDTO.class);
    }
    public UsuarioDTO adicionar(UsuarioCreateDTO usuarioCreateDTO) throws RegraDeNegocioException {

        validarEmail(usuarioCreateDTO.getEmail());

        UsuarioEntity usuarioEntity = new UsuarioEntity();
        AutenticacaoEntity autenticacaoEntity = new AutenticacaoEntity();

        usuarioEntity.setNome(usuarioCreateDTO.getNome());
        autenticacaoEntity.setEmail(usuarioCreateDTO.getEmail());
        autenticacaoEntity.setSenha(autenticacaoService.criptografarSenha(usuarioCreateDTO.getSenha()));

        UsuarioEntity usuario = usuarioRepository.save(usuarioEntity);

        autenticacaoEntity.setUsuarioEntity(usuario);
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
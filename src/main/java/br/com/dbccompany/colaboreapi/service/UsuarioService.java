package br.com.dbccompany.colaboreapi.service;

import br.com.dbccompany.colaboreapi.dto.usuario.UsuarioCreateDTO;
import br.com.dbccompany.colaboreapi.dto.usuario.UsuarioDTO;
import br.com.dbccompany.colaboreapi.dto.usuario.UsuarioSemSenhaDTO;
import br.com.dbccompany.colaboreapi.entity.UsuarioEntity;
import br.com.dbccompany.colaboreapi.exceptions.AmazonS3Exception;
import br.com.dbccompany.colaboreapi.exceptions.RegraDeNegocioException;
import br.com.dbccompany.colaboreapi.repository.UsuarioRepository;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.net.URI;
import java.util.Optional;

@Slf4j
@Service
@RequiredArgsConstructor
public class UsuarioService {

    private final ObjectMapper objectMapper;

    private final UsuarioRepository usuarioRepository;

    private final S3Service s3Service;

    public void adicionarFoto(MultipartFile multipartFile) throws AmazonS3Exception, RegraDeNegocioException, IOException {

        UsuarioEntity usuarioEntity = getLoggedUser();
        URI uri = s3Service.uploadFile(multipartFile);
        usuarioEntity.setFoto(uri.toString());
        usuarioRepository.save(usuarioEntity);
    }
    public UsuarioDTO adicionar(UsuarioCreateDTO usuarioCreateDTO) throws RegraDeNegocioException {

        validarEmail(usuarioCreateDTO.getEmail());

        UsuarioEntity usuarioEntity = new UsuarioEntity();

        usuarioEntity.setNome(usuarioCreateDTO.getNome());
        usuarioEntity.setEmail(usuarioCreateDTO.getEmail());
        usuarioEntity.setSenha(criptografarSenha(usuarioCreateDTO.getSenha()));

        UsuarioEntity usuario = usuarioRepository.save(usuarioEntity);

        usuarioRepository.save(usuarioEntity);

        return objectMapper.convertValue(usuario, UsuarioDTO.class);
    }

    public UsuarioSemSenhaDTO retornarUsuarioLogado() throws RegraDeNegocioException {
        UsuarioEntity usuarioEntity = getLoggedUser();
        return convertToSemSenhaDTO(usuarioEntity);
    }

    public void validarEmail(String emailParaValidar) throws RegraDeNegocioException {
        if (emailParaValidar.matches("^(.+)@dbccompany.com.br")) {
            log.info("email validado");
        } else {
            throw new RegraDeNegocioException("Padrão de e-mail incorreto");
        }
    }

    public UsuarioDTO findLoginById(Integer idUsuario) throws RegraDeNegocioException {
        UsuarioEntity usuarioEntity = findById(getIdLoggedUser());
        return convertToDTO(usuarioEntity);
    }

    private UsuarioEntity findById(Integer idUsuario) throws RegraDeNegocioException {
        UsuarioEntity usuarioEntity = usuarioRepository.findById(idUsuario)
                .orElseThrow(() ->
                        new RegraDeNegocioException( "Usuário não encontrado"));
        return usuarioEntity;
    }

    public UsuarioEntity getLoggedUser() throws RegraDeNegocioException {
        UsuarioEntity usuarioRecuperado = usuarioRepository.findById(getIdLoggedUser()).stream()
                .findFirst()
                .orElseThrow(() -> new RegraDeNegocioException("Usuário não encontrado"));
        return usuarioRecuperado;
    }

    public String criptografarSenha(String senha) {

        BCryptPasswordEncoder bCryptPasswordEncoder = new BCryptPasswordEncoder();
        String criptografia = bCryptPasswordEncoder.encode(senha);
        return criptografia;
    }

    public Optional<UsuarioEntity> findByEmail(String email) {
        return usuarioRepository.findByEmail(email);
    }

    public Integer getIdLoggedUser() {
        return (Integer) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
    }

    public UsuarioDTO convertToDTO (UsuarioEntity usuarioEntity) {
        return objectMapper.convertValue(usuarioEntity, UsuarioDTO.class);
    }

    public UsuarioSemSenhaDTO convertToSemSenhaDTO (UsuarioEntity usuarioEntity) {
        return objectMapper.convertValue(usuarioEntity, UsuarioSemSenhaDTO.class);
    }
}
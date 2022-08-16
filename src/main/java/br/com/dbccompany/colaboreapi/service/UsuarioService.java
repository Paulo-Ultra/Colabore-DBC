package br.com.dbccompany.colaboreapi.service;

import br.com.dbccompany.colaboreapi.dto.campanha.CampanhaDTO;
import br.com.dbccompany.colaboreapi.dto.usuario.UsuarioCreateDTO;
import br.com.dbccompany.colaboreapi.dto.usuario.UsuarioDTO;
import br.com.dbccompany.colaboreapi.entity.CampanhaEntity;
import br.com.dbccompany.colaboreapi.entity.UsuarioEntity;
import br.com.dbccompany.colaboreapi.exceptions.AmazonS3Exception;
import br.com.dbccompany.colaboreapi.exceptions.CampanhaNaoEncontradaException;
import br.com.dbccompany.colaboreapi.exceptions.RegraDeNegocioException;
import br.com.dbccompany.colaboreapi.repository.UsuarioRepository;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.net.URI;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
public class UsuarioService {

    private final ObjectMapper objectMapper;

    private final UsuarioRepository usuarioRepository;

    private final S3Service s3Service;

    public void adicionarFoto(MultipartFile multipartFile) throws AmazonS3Exception, RegraDeNegocioException {

        UsuarioEntity usuarioEntity = localizarUsuario(getIdUsuarioLogado());
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

//    public List<UsuarioDTO> listar() throws RegraDeNegocioException {
//        Integer idLoggedUser = getIdLoggedUser();
//        UsuarioEntity usuarioLogadoEntity = getIdLoggedUser();
//
//        Integer id = (Integer) usuarioLogadoEntity.getIdUsuario();
//
//        localizarUsuario(id);
//        return usuarioRepository.findById(id).stream()
//                .filter(usuario -> usuario.getIdUsuario().equals(id))
//                .map(this::convertToDTO)
//                .toList();
//    }

    public List<UsuarioDTO> dadosUsuarioLogado() throws RegraDeNegocioException {
        return usuarioRepository.findById(getIdUsuarioLogado())
                .stream().map(usuarioEntity -> {
                    UsuarioDTO usuarioDTO = convertToDTO(usuarioEntity);
                    return usuarioDTO;
                }).collect(Collectors.toList());
    }

    public void validarEmail(String emailParaValidar) throws RegraDeNegocioException {
        if (emailParaValidar.matches("^(.+)@dbccompany.com.br")) {
            log.info("email validado");
        } else {
            throw new RegraDeNegocioException("Padrão de e-mail incorreto");
        }
    }

    public UsuarioDTO findLoginById(Integer idUsuario) throws RegraDeNegocioException {
        UsuarioEntity usuarioEntity = usuarioRepository.findById(getIdUsuarioLogado())
                .orElseThrow(() ->
                        new RegraDeNegocioException( "Usuário não encontrado"));;
        return convertToDTO(usuarioEntity);
    }

    public UsuarioEntity localizarUsuario(Integer idUsuario) throws RegraDeNegocioException {
        UsuarioEntity usuarioRecuperado = usuarioRepository.findById(getIdUsuarioLogado()).stream()
                .filter(usuario -> usuario.getIdUsuario().equals(idUsuario))
                .findFirst()
                .orElseThrow(() -> new RegraDeNegocioException("Usuário não encontrado"));
        return usuarioRecuperado;
    }

    public Integer getIdUsuarioLogado() {
        Integer idLoggedUser = getIdLoggedUser();
        return idLoggedUser;
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
    public UsuarioDTO getLoggedUser() throws RegraDeNegocioException {
        return findLoginById(getIdLoggedUser());
    }
    public UsuarioDTO convertToDTO (UsuarioEntity usuarioEntity) {
        return objectMapper.convertValue(usuarioEntity, UsuarioDTO.class);
    }
}
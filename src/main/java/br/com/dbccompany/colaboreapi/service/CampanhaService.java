package br.com.dbccompany.colaboreapi.service;

import br.com.dbccompany.colaboreapi.dto.campanha.CampanhaCreateComFotoDTO;
import br.com.dbccompany.colaboreapi.dto.campanha.CampanhaCreateDTO;
import br.com.dbccompany.colaboreapi.dto.campanha.CampanhaDTO;
import br.com.dbccompany.colaboreapi.entity.CampanhaEntity;
import br.com.dbccompany.colaboreapi.entity.UsuarioEntity;
import br.com.dbccompany.colaboreapi.exceptions.AmazonS3Exception;
import br.com.dbccompany.colaboreapi.exceptions.CampanhaNaoEncontradaException;
import br.com.dbccompany.colaboreapi.exceptions.RegraDeNegocioException;
import br.com.dbccompany.colaboreapi.repository.CampanhaRepository;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.web.util.UriBuilder;

import java.math.BigDecimal;
import java.net.URI;
import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class CampanhaService {

    private final ObjectMapper objectMapper;
    private final UsuarioService usuarioService;
    private final CampanhaRepository campanhaRepository;

    private final S3Service s3Service;

    public CampanhaDTO adicionar(CampanhaCreateComFotoDTO campanhaCreateComFotoDTO) throws RegraDeNegocioException, AmazonS3Exception {

        Integer id = usuarioService.idUsuarioLogado();

        UsuarioEntity usuarioRecuperado = usuarioService.localizarUsuario(id);

        CampanhaEntity campanhaEntity = new CampanhaEntity();

        extracted(campanhaCreateComFotoDTO, campanhaEntity);

        URI uri = s3Service.uploadFile(campanhaCreateComFotoDTO.getFotoCampanha());
        campanhaEntity.setFotoCampanha(uri.toString());

        campanhaEntity.setUsuario(usuarioRecuperado);
        campanhaEntity.setIdUsuario(usuarioRecuperado.getIdUsuario());

        if (campanhaCreateComFotoDTO.getArrecadacao() == null) {
            campanhaEntity.setArrecadacao(BigDecimal.valueOf(0));
        }

        campanhaEntity.setUltimaAlteracao(LocalDateTime.now());

        return retornarDTO(campanhaRepository.save(campanhaEntity));
    }

    public CampanhaDTO editar(Integer id,
                              CampanhaCreateComFotoDTO campanhaCreateComFotoDTO) throws RegraDeNegocioException, CampanhaNaoEncontradaException, AmazonS3Exception {

        CampanhaEntity campanhaRecuperada = buscarIdCampanha(id);

        Integer idUsuario = usuarioService.idUsuarioLogado();

        UsuarioEntity usuarioCampanha = usuarioService.localizarUsuario(idUsuario);

        extracted(campanhaCreateComFotoDTO, campanhaRecuperada);

        campanhaRecuperada.setUltimaAlteracao(LocalDateTime.now());
        campanhaRecuperada.setIdUsuario(usuarioCampanha.getIdUsuario());

        URI uri = s3Service.uploadFile(campanhaCreateComFotoDTO.getFotoCampanha());
        campanhaRecuperada.setFotoCampanha(uri.toString());

        verificaCriadorDaCampanha(id);

        return retornarDTO(campanhaRepository.save(campanhaRecuperada));
    }

    private static void extracted(CampanhaCreateComFotoDTO campanhaCreateComFotoDTO, CampanhaEntity campanhaEntity) {
        campanhaEntity.setMeta(campanhaCreateComFotoDTO.getMeta());
        campanhaEntity.setDescricao(campanhaCreateComFotoDTO.getDescricao());
        campanhaEntity.setTitulo(campanhaCreateComFotoDTO.getTitulo());
        campanhaEntity.setStatusMeta(campanhaCreateComFotoDTO.getStatusMeta());
        campanhaEntity.setSituacao(campanhaCreateComFotoDTO.getSituacao());
    }

    public CampanhaDTO campanhaPeloId(Integer idCampanha) throws CampanhaNaoEncontradaException {
        CampanhaEntity campanhaEntity = buscarIdCampanha(idCampanha);
        CampanhaDTO campanhaDTO = retornarDTO(campanhaEntity);
            return campanhaDTO;
    }

    public List<CampanhaDTO> listaDeCampanhas() throws RegraDeNegocioException {
        Integer id = usuarioService.idUsuarioLogado();
        usuarioService.localizarUsuario(id);
        return campanhaRepository.findAll().stream()
                .map(campanhaEntity -> {
                    CampanhaDTO campanhaDTO = objectMapper.convertValue(campanhaEntity, CampanhaDTO.class);
                    return campanhaDTO;
                }).toList();
    }

    public List<CampanhaDTO> listaDeCampanhasByUsuarioLogado() throws RegraDeNegocioException {
        return campanhaRepository.findAllByIdUsuario(usuarioService.idUsuarioLogado())
                .stream().map(campanhaEntity -> {
                    CampanhaDTO campanhaDTO = retornarDTO(campanhaEntity);
                    return campanhaDTO;
                }).collect(Collectors.toList());
    }

    public void deletar(Integer id) throws CampanhaNaoEncontradaException, RegraDeNegocioException {
        CampanhaEntity campanhaEntity = buscarIdCampanha(id);
        verificaCriadorDaCampanha(id);
        campanhaRepository.delete(campanhaEntity);
    }

     private CampanhaDTO retornarDTO(CampanhaEntity campanhaEntity) {
        return objectMapper.convertValue(campanhaEntity, CampanhaDTO.class);
    }

    private CampanhaEntity retornarEntity(CampanhaCreateDTO campanhaCreateDTO) {
        return objectMapper.convertValue(campanhaCreateDTO, CampanhaEntity.class);
    }

    private CampanhaEntity buscarIdCampanha(Integer id) throws CampanhaNaoEncontradaException {
        return campanhaRepository.findById(id).orElseThrow(() -> new CampanhaNaoEncontradaException("Campanha não encontrada."));
    }

    private void verificaCriadorDaCampanha(Integer idCampanha) throws CampanhaNaoEncontradaException, RegraDeNegocioException {
        campanhaRepository.findAllByIdUsuarioAndIdCampanha(usuarioService.idUsuarioLogado(), idCampanha)
                .stream()
                .findFirst()
                .orElseThrow(() -> new CampanhaNaoEncontradaException("Campanha não encontrada"));
    }

    public CampanhaEntity localizarCampanha(Integer idCampanha) throws RegraDeNegocioException {
        CampanhaEntity campanhaRecuperada = campanhaRepository.findAll().stream()
                .filter(campanha -> campanha.getIdCampanha().equals(idCampanha))
                .findFirst()
                .orElseThrow(() -> new RegraDeNegocioException("Campanha não encontrada"));
        return campanhaRecuperada;
    }
}

package br.com.dbccompany.colaboreapi.service;

import br.com.dbccompany.colaboreapi.dto.campanha.CampanhaCreateDTO;
import br.com.dbccompany.colaboreapi.dto.campanha.CampanhaDTO;
import br.com.dbccompany.colaboreapi.dto.campanha.DoadorCampanhaDTO;
import br.com.dbccompany.colaboreapi.dto.doador.DoadorDTO;
import br.com.dbccompany.colaboreapi.dto.usuario.UsuarioSemSenhaDTO;
import br.com.dbccompany.colaboreapi.entity.CampanhaEntity;
import br.com.dbccompany.colaboreapi.entity.UsuarioEntity;
import br.com.dbccompany.colaboreapi.enums.TipoFiltro;
import br.com.dbccompany.colaboreapi.exceptions.AmazonS3Exception;
import br.com.dbccompany.colaboreapi.exceptions.CampanhaNaoEncontradaException;
import br.com.dbccompany.colaboreapi.exceptions.RegraDeNegocioException;
import br.com.dbccompany.colaboreapi.repository.CampanhaRepository;
import br.com.dbccompany.colaboreapi.repository.UsuarioRepository;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.math.BigDecimal;
import java.net.URI;
import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class CampanhaService {

    private final ObjectMapper objectMapper;
    private final UsuarioService usuarioService;
    private final CampanhaRepository campanhaRepository;

    private final UsuarioRepository usuarioRepository;

    private final S3Service s3Service;

    public CampanhaDTO adicionar(CampanhaCreateDTO campanhaCreateDTO) throws RegraDeNegocioException {

        usuarioService.getLoggedUser();

        CampanhaEntity campanhaEntity = objectMapper.convertValue(campanhaCreateDTO, CampanhaEntity.class);

        if (campanhaCreateDTO.getArrecadacao() == null) {
            campanhaEntity.setArrecadacao(BigDecimal.valueOf(0));
        }

        campanhaEntity.setUltimaAlteracao(LocalDateTime.now());
        campanhaEntity.setStatusMeta(false);

        return retornarDTO(campanhaRepository.save(campanhaEntity));
    }

    public void adicionarFoto(Integer idCampanha, MultipartFile multipartFile) throws AmazonS3Exception, RegraDeNegocioException {
        CampanhaEntity campanhaEntity = localizarCampanha(idCampanha);
        URI uri = s3Service.uploadFile(multipartFile);
        campanhaEntity.setFotoCampanha(uri.toString());
        campanhaRepository.save(campanhaEntity);
    }

    public CampanhaDTO editar(Integer id,
                              CampanhaCreateDTO campanhaCreateDTO) throws RegraDeNegocioException, CampanhaNaoEncontradaException {

        CampanhaEntity campanhaRecuperada = buscarIdCampanha(id);

        if (campanhaRecuperada.getArrecadacao().doubleValue() > 0) {
            throw new RegraDeNegocioException("Não é possível alterar campanhas que receberam doações");
        }

        UsuarioEntity usuarioCampanha = usuarioService.getLoggedUser();

        validaAlteracoes(campanhaCreateDTO, campanhaRecuperada);

        campanhaRecuperada.setUltimaAlteracao(LocalDateTime.now());
        campanhaRecuperada.setIdUsuario(usuarioCampanha.getIdUsuario());

        verificaCriadorDaCampanha(id);

        return retornarDTO(campanhaRepository.save(campanhaRecuperada));
    }

    public CampanhaDTO campanhaPeloId(Integer idCampanha) throws CampanhaNaoEncontradaException {
        CampanhaEntity campanhaEntity = buscarIdCampanha(idCampanha);
        CampanhaDTO campanhaDTO = retornarDTO(campanhaEntity);
        return campanhaDTO;
    }

    public CampanhaEntity localizarCampanha(Integer idCampanha) throws RegraDeNegocioException {
        return campanhaRepository.findById(idCampanha)
                .orElseThrow(() -> new RegraDeNegocioException("Campanha não localizada"));
    }

    public List<CampanhaDTO> listaDeCampanhasByUsuarioLogado() {
        return campanhaRepository.findAllByIdUsuario(usuarioService.getIdLoggedUser())
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


    public List<CampanhaDTO> listarCampanha (TipoFiltro tipoFiltro, boolean minhasContribuicoes, boolean minhasCampanhas) {

        Integer idUsuario = usuarioService.getIdLoggedUser();

        if (tipoFiltro.equals(TipoFiltro.META_NAO_ATINGIDA)) {
            return getCampanhaComDoacoesDTOS(campanhaRepository.findAll(false, idUsuario, minhasContribuicoes, minhasCampanhas));
        } else if (tipoFiltro.equals(TipoFiltro.META_ATINGIDA)) {
            return getCampanhaComDoacoesDTOS(campanhaRepository.findAll(true, idUsuario, minhasContribuicoes, minhasCampanhas));
        } else {
            return getCampanhaComDoacoesDTOS(campanhaRepository.findAll(null, idUsuario, minhasContribuicoes, minhasCampanhas));
        }
    }

    private List<CampanhaDTO> getCampanhaComDoacoesDTOS(List<CampanhaEntity> campanhaRepository) {
        return campanhaRepository.stream()
                .map(campanhaEntity -> {
                    CampanhaDTO campanhaDTO = retornarDTO(campanhaEntity);
                    campanhaDTO.getStatusMeta();
                    campanhaDTO.setDoacoes(campanhaEntity.getDoadores().stream()
                            .map(doadorEntity -> {
                                DoadorCampanhaDTO doadorCampanhaDTO = objectMapper.convertValue(doadorEntity, DoadorCampanhaDTO.class);
                                UsuarioEntity usuarioEntity = doadorEntity.getUsuario();
                                doadorCampanhaDTO.setNome(usuarioEntity.getNome());
                                doadorCampanhaDTO.setFoto(usuarioEntity.getFoto());
                                return doadorCampanhaDTO;
                            })
                            .collect(Collectors.toList()));
                    return campanhaDTO;
                }).collect(Collectors.toList());
    }

    private CampanhaDTO retornarDTO(CampanhaEntity campanhaEntity) {
        return objectMapper.convertValue(campanhaEntity, CampanhaDTO.class);
    }

    private CampanhaEntity buscarIdCampanha(Integer id) throws CampanhaNaoEncontradaException {
        return campanhaRepository.findById(id).orElseThrow(() -> new CampanhaNaoEncontradaException("Campanha não encontrada."));
    }

    private void verificaCriadorDaCampanha(Integer idCampanha) throws CampanhaNaoEncontradaException {
        campanhaRepository.findAllByIdUsuarioAndIdCampanha(usuarioService.getIdLoggedUser(), idCampanha)
                .stream()
                .findFirst()
                .orElseThrow(() -> new CampanhaNaoEncontradaException("Campanha não encontrada"));
    }

    private static void validaAlteracoes(CampanhaCreateDTO campanhaCreateDTO, CampanhaEntity campanhaEntity) {
        campanhaEntity.setMeta(campanhaCreateDTO.getMeta());
        campanhaEntity.setDescricao(campanhaCreateDTO.getDescricao());
        campanhaEntity.setTitulo(campanhaCreateDTO.getTitulo());
    }

    private UsuarioEntity buscarIdUsuario(Integer id) throws RegraDeNegocioException {
        return usuarioRepository.findById(id).orElseThrow(() -> new RegraDeNegocioException("Usuario não encontrado."));
    }

}

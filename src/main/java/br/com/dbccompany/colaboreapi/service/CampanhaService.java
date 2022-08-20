package br.com.dbccompany.colaboreapi.service;

import br.com.dbccompany.colaboreapi.dto.campanha.CampanhaCreateDTO;
import br.com.dbccompany.colaboreapi.dto.campanha.CampanhaDTO;
import br.com.dbccompany.colaboreapi.dto.campanha.DoadorCampanhaDTO;
import br.com.dbccompany.colaboreapi.dto.tag.TagCreateDTO;
import br.com.dbccompany.colaboreapi.entity.CampanhaEntity;
import br.com.dbccompany.colaboreapi.entity.TagEntity;
import br.com.dbccompany.colaboreapi.entity.UsuarioEntity;
import br.com.dbccompany.colaboreapi.enums.TipoFiltro;
import br.com.dbccompany.colaboreapi.exceptions.AmazonS3Exception;
import br.com.dbccompany.colaboreapi.exceptions.CampanhaException;
import br.com.dbccompany.colaboreapi.exceptions.RegraDeNegocioException;
import br.com.dbccompany.colaboreapi.repository.CampanhaRepository;
import br.com.dbccompany.colaboreapi.repository.TagRepository;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.math.BigDecimal;
import java.net.URI;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class CampanhaService {

    private final ObjectMapper objectMapper;

    private final UsuarioService usuarioService;
    private final TagService tagService;

    private final TagRepository tagRepository;
    private final CampanhaRepository campanhaRepository;
    private final S3Service s3Service;

    public CampanhaDTO adicionar(CampanhaDTO campanhaDTO) throws RegraDeNegocioException, CampanhaException {

        if (campanhaDTO.getMeta().doubleValue() <= 0) {
            throw new RegraDeNegocioException("A meta da campanha deve ser maior do que 0!");
        }

        UsuarioEntity usuarioEntity = usuarioService.getLoggedUser();

        CampanhaEntity campanhaEntity = objectMapper.convertValue(campanhaDTO, CampanhaEntity.class);

        if (campanhaDTO.getArrecadacao() == null) {
            campanhaEntity.setArrecadacao(BigDecimal.valueOf(0));
        }
        campanhaEntity.setUsuario(usuarioEntity);
        campanhaEntity.setIdUsuario(usuarioService.getIdLoggedUser());
        campanhaEntity.setStatusMeta(false);
        campanhaEntity.setUltimaAlteracao(LocalDateTime.now());
        if(campanhaEntity.getDataLimite().isBefore(LocalDateTime.now()) || campanhaEntity.getDataLimite().isEqual(LocalDateTime.now())){
            throw new CampanhaException("A campanha deve ser criada com uma data posterior a de hoje");
        }

        Set<TagEntity> tagEntities = campanhaDTO.getTags().stream()
                .map(tagString -> {
                    Optional<TagEntity> nomeTag = tagService.findByNomeTag(tagString);
                    if(nomeTag.isPresent()){
                        return nomeTag.get();
                    }
                    else {
                        return tagService.adicionar(new TagCreateDTO(tagString));
                    }
                })
                .collect(Collectors.toSet());

        campanhaEntity.setTagEntities(tagEntities);
        campanhaRepository.save(campanhaEntity);
        CampanhaDTO campanhaDTO1 = getCampanhaByIdDTO(campanhaEntity);
        return campanhaDTO1;
    }

    public void adicionarFoto(Integer idCampanha, MultipartFile multipartFile) throws AmazonS3Exception, CampanhaException, IOException {
        CampanhaEntity campanhaEntity = buscarIdCampanha(idCampanha);
        URI uri = s3Service.uploadFile(multipartFile);
        campanhaEntity.setFotoCampanha(uri.toString());
        campanhaRepository.save(campanhaEntity);
    }

    public CampanhaDTO editar(Integer id,
                              CampanhaDTO campanhaDTO) throws RegraDeNegocioException, CampanhaException {

        CampanhaEntity campanhaRecuperada = buscarIdCampanha(id);

        if (campanhaRecuperada.getArrecadacao().doubleValue() > 0) {
            throw new RegraDeNegocioException("Não é possível alterar campanhas que receberam doações");
        }

        UsuarioEntity usuarioCampanha = usuarioService.getLoggedUser();

        validaAlteracoes(campanhaDTO, campanhaRecuperada);

        Set<TagEntity> tagEntities = campanhaDTO.getTags().stream()
                .map(tagString -> {
                    Optional<TagEntity> nomeTag = tagService.findByNomeTag(tagString);
                    if(nomeTag.isPresent()){
                        return nomeTag.get();
                    }
                    else {
                        return tagService.adicionar(new TagCreateDTO(tagString));
                    }
                })
                .collect(Collectors.toSet());

        campanhaRecuperada.setUltimaAlteracao(LocalDateTime.now());
        campanhaRecuperada.setIdUsuario(usuarioCampanha.getIdUsuario());
        campanhaRecuperada.setTagEntities(tagEntities);
        verificaCriadorDaCampanha(id);

        return getCampanhaByIdDTO(campanhaRepository.save(campanhaRecuperada));
    }

    public CampanhaDTO localizarCampanha(Integer idCampanha) throws CampanhaException {
        CampanhaEntity campanhaEntity = buscarIdCampanha(idCampanha);
        return getCampanhaByIdDTO(campanhaEntity);
    }

    public List<CampanhaDTO> listaDeCampanhasByUsuarioLogado() {
        return campanhaRepository.findAllByIdUsuario(usuarioService.getIdLoggedUser())
                .stream().map(campanhaEntity -> {
                    CampanhaDTO campanhaDTO = getCampanhaByIdDTO(campanhaEntity);
                    return campanhaDTO;
                }).collect(Collectors.toList());
    }


    public void deletar(Integer id) throws CampanhaException {
        CampanhaEntity campanhaEntity = buscarIdCampanha(id);
        verificaCriadorDaCampanha(id);
        campanhaRepository.delete(campanhaEntity);
    }

    public List<CampanhaDTO> listarCampanha (TipoFiltro tipoFiltro, boolean minhasContribuicoes, boolean minhasCampanhas, List<Integer> idTags) {

        Integer idUsuario = usuarioService.getIdLoggedUser();

        if (tipoFiltro.equals(TipoFiltro.META_NAO_ATINGIDA)) {
            return getCampanhaComDoacoesTagsDTOS(campanhaRepository.findAll(false, idUsuario, idTags,minhasContribuicoes, minhasCampanhas));
        } else if (tipoFiltro.equals(TipoFiltro.META_ATINGIDA)) {
            return getCampanhaComDoacoesTagsDTOS(campanhaRepository.findAll(true, idUsuario, idTags,minhasContribuicoes, minhasCampanhas));
        } else {
            return getCampanhaComDoacoesTagsDTOS(campanhaRepository.findAll(null, idUsuario, idTags,minhasContribuicoes, minhasCampanhas));
        }
    }

    private List<CampanhaDTO> getCampanhaComDoacoesTagsDTOS(List<CampanhaEntity> campanhaRepository) {
        return campanhaRepository.stream()
                .map(campanhaEntity -> getCampanhaByIdDTO(campanhaEntity)).collect(Collectors.toList());
    }

    private CampanhaDTO getCampanhaByIdDTO(CampanhaEntity campanhaEntity) {
        CampanhaDTO campanhaDTO = objectMapper.convertValue(campanhaEntity, CampanhaDTO.class);
        campanhaDTO.getStatusMeta();
        campanhaDTO.setNome(campanhaEntity.getUsuario().getNome());
        if(campanhaEntity.getDoadores() != null) {
            campanhaDTO.setDoacoes(campanhaEntity.getDoadores().stream()
                    .map(doadorEntity -> {
                        DoadorCampanhaDTO doadorCampanhaDTO = objectMapper.convertValue(doadorEntity, DoadorCampanhaDTO.class);
                        UsuarioEntity usuarioEntity = doadorEntity.getUsuario();
                        doadorCampanhaDTO.setNome(usuarioEntity.getNome());
                        doadorCampanhaDTO.setFoto(usuarioEntity.getFoto());
                        return doadorCampanhaDTO;
                    })
                    .collect(Collectors.toList()));
        }
        if(campanhaEntity.getTagEntities() != null) {
            campanhaDTO.setTags(campanhaEntity.getTagEntities().stream()
                    .map(tagEntity -> tagEntity.getNomeTag())
                    .collect(Collectors.toSet()));
        }
        return campanhaDTO;
    }

    public CampanhaEntity buscarIdCampanha(Integer id) throws CampanhaException {
        return campanhaRepository.findById(id).orElseThrow(() -> new CampanhaException("Campanha não encontrada."));
    }

    public void verificaCriadorDaCampanha(Integer idCampanha) throws CampanhaException {
        campanhaRepository.findAllByIdUsuarioAndIdCampanha(usuarioService.getIdLoggedUser(), idCampanha)
                .stream()
                .findFirst()
                .orElseThrow(() -> new CampanhaException("Campanha não encontrada"));
    }

    private static void validaAlteracoes(CampanhaCreateDTO campanhaCreateDTO, CampanhaEntity campanhaEntity) {
        campanhaEntity.setMeta(campanhaCreateDTO.getMeta());
        campanhaEntity.setDescricao(campanhaCreateDTO.getDescricao());
        campanhaEntity.setTitulo(campanhaCreateDTO.getTitulo());
    }

}

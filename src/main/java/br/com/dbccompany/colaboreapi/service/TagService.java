package br.com.dbccompany.colaboreapi.service;

import br.com.dbccompany.colaboreapi.dto.doador.DoadorDTO;
import br.com.dbccompany.colaboreapi.dto.tag.TagCreateDTO;
import br.com.dbccompany.colaboreapi.dto.tag.TagDTO;
import br.com.dbccompany.colaboreapi.entity.CampanhaEntity;
import br.com.dbccompany.colaboreapi.entity.DoadorEntity;
import br.com.dbccompany.colaboreapi.entity.TagEntity;
import br.com.dbccompany.colaboreapi.entity.UsuarioEntity;
import br.com.dbccompany.colaboreapi.exceptions.RegraDeNegocioException;
import br.com.dbccompany.colaboreapi.repository.DoadorRepository;
import br.com.dbccompany.colaboreapi.repository.TagRepository;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.Set;

@Service
@RequiredArgsConstructor
public class TagService {

    private final DoadorRepository doadorRepository;

    private final UsuarioService usuarioService;

    private final CampanhaService campanhaService;

    private final TagRepository tagRepository;

    private final ObjectMapper objectMapper;

    public TagDTO adicionar(Integer idCampanha, TagCreateDTO tagCreateDTO) throws RegraDeNegocioException {

        try{
            String nomeTag = tagCreateDTO.getNomeTag().toLowerCase();

            CampanhaEntity campanhaEntity = campanhaService.localizarCampanha(idCampanha);
            TagEntity tagEntity = new TagEntity();

            tagEntity.setNomeTag(nomeTag);

            Set<CampanhaEntity> campanhas = Set.of(campanhaEntity);


            tagEntity.setCampanhaEntities(campanhas);
            tagEntity.setIdCampanha(idCampanha);

            tagRepository.save(tagEntity);

            return retornarDoadorDTO(tagEntity);
        } catch (RegraDeNegocioException ex){
            throw new RegraDeNegocioException("Esta tag já existe!");
        }
    }

    public TagDTO retornarDoadorDTO (TagEntity tagEntity) {
        return objectMapper.convertValue(tagEntity, TagDTO.class);
    }

}

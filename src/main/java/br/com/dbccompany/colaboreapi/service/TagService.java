package br.com.dbccompany.colaboreapi.service;

import br.com.dbccompany.colaboreapi.dto.tag.TagCreateDTO;
import br.com.dbccompany.colaboreapi.dto.tag.TagDTO;

import br.com.dbccompany.colaboreapi.entity.TagEntity;
import br.com.dbccompany.colaboreapi.exceptions.RegraDeNegocioException;
import br.com.dbccompany.colaboreapi.repository.TagRepository;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class TagService {

    private final TagRepository tagRepository;

    private final ObjectMapper objectMapper;

    public TagDTO adicionar(TagCreateDTO tagCreateDTO) throws RegraDeNegocioException {

        tagCreateDTO.setNomeTag(tagCreateDTO.getNomeTag().toLowerCase());

        Integer verificaExistenciaTag = tagRepository.findByNomeTag(tagCreateDTO.getNomeTag());

        if(verificaExistenciaTag > 0 ){
            throw new RegraDeNegocioException("Esta tag já existe!");
        }
        if(tagCreateDTO.getNomeTag().isBlank() || tagCreateDTO.getNomeTag().isEmpty() ){
            throw new RegraDeNegocioException("Nome da tag não pode ser nula ou vazia");
        }
        TagEntity tagEntity = objectMapper.convertValue(tagCreateDTO, TagEntity.class);
        TagEntity novaTag = tagRepository.save(tagEntity);
        TagDTO tagDTO = objectMapper.convertValue(novaTag, TagDTO.class);

        return tagDTO;
    }

    public List<TagDTO> list() {
        return tagRepository.findAll().stream()
                .map(tag -> objectMapper.convertValue(tag, TagDTO.class))
                .collect(Collectors.toList());
    }

    public TagEntity findById(Integer id) throws RegraDeNegocioException {
        TagEntity tagEntity = tagRepository.findById(id)
                .orElseThrow(() -> new RegraDeNegocioException("Tag não localizada!"));
        return tagEntity;
    }

    public void delete(Integer id) throws RegraDeNegocioException {
        TagEntity tagEntity = findById(id);
        tagRepository.delete(tagEntity);
    }

    public List<TagDTO> listTagCampanha(Integer idCampanha) {
        return tagRepository.listTagCampanha(idCampanha).stream()
                .map(tag -> objectMapper.convertValue(tag, TagDTO.class))
                .collect(Collectors.toList());
    }
}

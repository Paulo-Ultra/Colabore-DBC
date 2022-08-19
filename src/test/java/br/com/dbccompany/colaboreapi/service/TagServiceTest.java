package br.com.dbccompany.colaboreapi.service;

import br.com.dbccompany.colaboreapi.dto.tag.TagCreateDTO;
import br.com.dbccompany.colaboreapi.dto.tag.TagDTO;
import br.com.dbccompany.colaboreapi.entity.CampanhaEntity;
import br.com.dbccompany.colaboreapi.entity.DoadorEntity;
import br.com.dbccompany.colaboreapi.entity.TagEntity;
import br.com.dbccompany.colaboreapi.entity.UsuarioEntity;
import br.com.dbccompany.colaboreapi.exceptions.RegraDeNegocioException;
import br.com.dbccompany.colaboreapi.repository.TagRepository;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.util.ReflectionTestUtils;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.*;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ContextConfiguration(classes = {CampanhaService.class})
@RunWith(MockitoJUnitRunner.class)
public class TagServiceTest {

    @InjectMocks
    private TagService tagService;

    @Mock
    private TagRepository tagRepository;

    private ObjectMapper objectMapper = new ObjectMapper();

    @Before
    public void init() {
        objectMapper.registerModule(new JavaTimeModule());
        objectMapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
        objectMapper.configure(SerializationFeature.FAIL_ON_EMPTY_BEANS, false);
        objectMapper.disable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS);
        ReflectionTestUtils.setField(tagService, "objectMapper", objectMapper);
    }

    @Test
    public void deveTestarAdicionarComSucesso() throws RegraDeNegocioException {

        when(tagRepository.findByNomeTag(anyString())).thenReturn(getTagEntitySemId().getIdTag());
        when(tagRepository.save(any(TagEntity.class))).thenReturn(getTagEntitySemId());

        TagDTO tagDTO = tagService.adicionar(getTagCreateDTO());

        assertNotNull(tagDTO);
        assertEquals(getTagEntitySemId().getNomeTag(), tagDTO.getNomeTag());
    }

    @Test(expected = RegraDeNegocioException.class)
    public void deveTestarAdicionarComExceptionTagExiste() throws RegraDeNegocioException {

        when(tagRepository.findByNomeTag(anyString())).thenReturn(getTagEntity().getIdTag());

        tagService.adicionar(getTagCreateDTO());
    }

    @Test(expected = RegraDeNegocioException.class)
    public void deveTestarAdicionarComExceptionTagNulaOuVazia() throws RegraDeNegocioException {

        when(tagRepository.findByNomeTag(anyString())).thenReturn(getTagEntitySemNome().getIdTag());

        tagService.adicionar(getTagCreateDTOSemNome());

    }

    @Test
    public void deveTestarListComSucesso(){

        when(tagRepository.findAll()).thenReturn(List.of(getTagEntity()));
        List<TagDTO> tagDTO = tagService.list();
        assertNotNull(tagDTO);
    }

    @Test
    public void deveTestarFindByIdComSucesso() throws RegraDeNegocioException {
        TagEntity tagEntity = getTagEntity();

        when(tagRepository.findById(anyInt())).thenReturn(Optional.of(tagEntity));

        tagService.findById(tagEntity.getIdTag());
    }

    @Test
    public void deveTestarDeleteComSucesso() throws RegraDeNegocioException {
        TagEntity tagEntity = getTagEntity();

        when(tagRepository.findById(anyInt())).thenReturn(Optional.of(tagEntity));

        tagService.delete(tagEntity.getIdTag());
    }

    @Test(expected = RegraDeNegocioException.class)
    public void deveTestarFindByIdComException() throws RegraDeNegocioException {

        tagService.findById(null);
    }



    public static DoadorEntity getDoadorEntity() {
        DoadorEntity doadorEntity = new DoadorEntity();
        doadorEntity.setIdDoador(1);
        doadorEntity.setUsuario(getUsuarioEntity());
        doadorEntity.setValor(new BigDecimal(500));
        doadorEntity.setCampanhas(Set.of(getCampanhaEntityNaoEncerraAutomatico()));
        return doadorEntity;
    }

    public static UsuarioEntity getUsuarioEntity() {
        UsuarioEntity usuarioEntity = new UsuarioEntity();
        usuarioEntity.setIdUsuario(1);
        usuarioEntity.setCampanhas(new HashSet<>());
        usuarioEntity.setFoto("foto 3x4.jpeg");
        usuarioEntity.setDoadores(new HashSet<>());
        usuarioEntity.setNome("Teste");
        usuarioEntity.setSenha("123");
        usuarioEntity.setEmail("teste@dbccompany.com.br");
        return usuarioEntity;
    }

    public static CampanhaEntity getCampanhaEntityNaoEncerraAutomatico() {
        CampanhaEntity campanhaEntity = new CampanhaEntity();
        campanhaEntity.setIdCampanha(1);
        campanhaEntity.setFotoCampanha("foto_capa.jpeg");
        campanhaEntity.setArrecadacao(new BigDecimal(200.00));
        campanhaEntity.setTitulo("Livros usados");
        campanhaEntity.setDescricao("Doação de livros usados");
        campanhaEntity.setUltimaAlteracao(LocalDateTime.now());
        campanhaEntity.setMeta(new BigDecimal(2000.00));
        campanhaEntity.setUsuario(getUsuarioEntity());
        campanhaEntity.setDoadores(new HashSet<>());
        campanhaEntity.setIdUsuario(1);
        campanhaEntity.setDataLimite(LocalDateTime.of(2022, 9, 17, 23, 59, 59));
        campanhaEntity.setEncerrarAutomaticamente(false);
        campanhaEntity.setTagEntities(Set.of(getTagEntitySemId()));
        campanhaEntity.setStatusMeta(false);
        return campanhaEntity;
    }

    public static CampanhaEntity getCampanhaEntityEncerraAutomatico() {
        CampanhaEntity campanhaEntity = new CampanhaEntity();
        campanhaEntity.setIdCampanha(1);
        campanhaEntity.setFotoCampanha("foto_capa.jpeg");
        campanhaEntity.setArrecadacao(new BigDecimal(200.00));
        campanhaEntity.setTitulo("Livros usados");
        campanhaEntity.setDescricao("Doação de livros usados");
        campanhaEntity.setUltimaAlteracao(LocalDateTime.now());
        campanhaEntity.setMeta(new BigDecimal(2000.00));
        campanhaEntity.setUsuario(getUsuarioEntity());
        campanhaEntity.setDoadores(new HashSet<>());
        campanhaEntity.setIdUsuario(1);
        campanhaEntity.setDataLimite(LocalDateTime.of(2022, 9, 17, 23, 59, 59));
        campanhaEntity.setEncerrarAutomaticamente(true);
        campanhaEntity.setTagEntities(new HashSet<>());
        campanhaEntity.setStatusMeta(false);
        return campanhaEntity;
    }
    public static TagEntity getTagEntitySemId() {
        TagEntity tagEntity = new TagEntity();
        tagEntity.setIdTag(0);
        tagEntity.setNomeTag("tagTeste1");
        tagEntity.setCampanhaEntities(Set.of(getCampanhaEntityEncerraAutomatico()));
        return tagEntity;
    }

    public static TagEntity getTagEntity() {
        TagEntity tagEntity = new TagEntity();
        tagEntity.setIdTag(1);
        tagEntity.setNomeTag("tagTeste1");
        tagEntity.setCampanhaEntities(Set.of(getCampanhaEntityEncerraAutomatico()));
        return tagEntity;
    }

    public static TagCreateDTO getTagCreateDTO() {
        TagCreateDTO tagCreateDTO = new TagCreateDTO();
        tagCreateDTO.setNomeTag("tagTeste1");
        return tagCreateDTO;
    }

    public static TagEntity getTagEntitySemNome() {
        TagEntity tagEntity = new TagEntity();
        tagEntity.setIdTag(0);
        tagEntity.setNomeTag("  ");
        tagEntity.setCampanhaEntities(Set.of(getCampanhaEntityEncerraAutomatico()));
        return tagEntity;
    }

    public static TagCreateDTO getTagCreateDTOSemNome() {
        TagCreateDTO tagCreateDTO = new TagCreateDTO();
        tagCreateDTO.setNomeTag("  ");
        return tagCreateDTO;
    }
}

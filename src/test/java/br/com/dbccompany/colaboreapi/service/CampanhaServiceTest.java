package br.com.dbccompany.colaboreapi.service;

import br.com.dbccompany.colaboreapi.dto.campanha.CampanhaDTO;
import br.com.dbccompany.colaboreapi.dto.tag.TagCreateDTO;
import br.com.dbccompany.colaboreapi.dto.tag.TagDTO;
import br.com.dbccompany.colaboreapi.entity.CampanhaEntity;
import br.com.dbccompany.colaboreapi.entity.DoadorEntity;
import br.com.dbccompany.colaboreapi.entity.TagEntity;
import br.com.dbccompany.colaboreapi.entity.UsuarioEntity;
import br.com.dbccompany.colaboreapi.enums.TipoFiltro;
import br.com.dbccompany.colaboreapi.exceptions.AmazonS3Exception;
import br.com.dbccompany.colaboreapi.exceptions.CampanhaException;
import br.com.dbccompany.colaboreapi.exceptions.RegraDeNegocioException;
import br.com.dbccompany.colaboreapi.repository.CampanhaRepository;
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
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.util.ReflectionTestUtils;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.math.BigDecimal;
import java.net.MalformedURLException;
import java.net.URI;
import java.net.URL;
import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import java.util.Set;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

@ContextConfiguration(classes = {CampanhaService.class})
@RunWith(MockitoJUnitRunner.class)
public class CampanhaServiceTest {

    @InjectMocks
    private CampanhaService campanhaService;

    @Mock
    private CampanhaRepository campanhaRepository;

    @Mock
    private S3Service s3Service;
    @Mock
    private UsuarioService usuarioService;

    private MockMultipartFile mockMultipartFile;
    private URL url;

    private final String bucketName = "";
    @Mock
    private TagService tagService;

    private ObjectMapper objectMapper = new ObjectMapper();

    @Before
    public void init() throws MalformedURLException {
        objectMapper.registerModule(new JavaTimeModule());
        objectMapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
        objectMapper.configure(SerializationFeature.FAIL_ON_EMPTY_BEANS, false);
        objectMapper.disable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS);
        ReflectionTestUtils.setField(campanhaService, "objectMapper", objectMapper);

        mockMultipartFile = new MockMultipartFile(
                "foto.pdf",
                "arquivo",
                "application/jpg",
                "{key1: value1}".getBytes());

        url = new URL(
                "https",
                "stackoverflow.com",
                80, "pages/page1.html");
    }

    @Test
    public void deveTestarCreateComSucesso() throws RegraDeNegocioException, CampanhaException {

        UsuarioEntity usuarioEntity = getUsuarioEntity();
        CampanhaEntity campanhaEntity = getCampanhaEntityEncerraAutomatico();

        TagEntity tagEntity = new TagEntity();
        tagEntity.setNomeTag("livros");

        CampanhaDTO campanhaDTO = getCampanhaDTOStatusEncerrada();

        TagCreateDTO tagCreateDTO = new TagCreateDTO();
        tagCreateDTO.setNomeTag("livros");

        Set<String> stringSet = Set.of("livros");

        campanhaDTO.setTags(stringSet);
        campanhaDTO.setArrecadacao(null);
        campanhaEntity.setUsuario(usuarioEntity);
        campanhaEntity.setIdUsuario(usuarioEntity.getIdUsuario());

        when(usuarioService.getLoggedUser()).thenReturn(usuarioEntity);
        when(tagService.findByNomeTag("livros")).thenReturn(Optional.of(tagEntity));
        when(campanhaRepository.save(any(CampanhaEntity.class))).thenReturn(campanhaEntity);

        campanhaService.adicionar(campanhaDTO);

        assertNotNull(campanhaDTO);
    }

    @Test(expected = RegraDeNegocioException.class)
    public void deveTestarCreateSemSucesso() throws RegraDeNegocioException, CampanhaException {
        CampanhaEntity campanhaEntity = getCampanhaEntityEncerraAutomatico();
        CampanhaDTO campanhaDTO = getCampanhaDTOStatusEncerrada();

        campanhaDTO.setMeta(new BigDecimal(0));
        campanhaDTO.setArrecadacao(new BigDecimal(50));

        campanhaService.adicionar(campanhaDTO);

        assertNotNull(campanhaDTO);
        assertEquals(campanhaEntity.getIdCampanha(), campanhaDTO.getIdCampanha());
        assertEquals(campanhaEntity.getArrecadacao(), campanhaDTO.getArrecadacao());
        assertEquals(campanhaEntity.getMeta(), campanhaDTO.getMeta());
        assertEquals(campanhaEntity.getFotoCampanha(), campanhaDTO.getFotoCampanha());
        assertEquals(campanhaEntity.getEncerrarAutomaticamente(), campanhaDTO.getEncerrarAutomaticamente());
        assertEquals(campanhaEntity.getDataLimite(), campanhaDTO.getDataLimite());
        assertEquals(campanhaEntity.getStatusMeta(), campanhaDTO.getStatusMeta());
        assertEquals(campanhaEntity.getDescricao(), campanhaDTO.getDescricao());
        assertEquals(campanhaEntity.getTitulo(), campanhaDTO.getTitulo());
    }

    @Test(expected = CampanhaException.class)
    public void deveTestarCreateSemSucessoDataPosterior() throws RegraDeNegocioException, CampanhaException {
        UsuarioEntity usuarioEntity = getUsuarioEntity();
        CampanhaEntity campanhaEntity = getCampanhaEntityEncerraAutomatico();
        TagEntity tagEntity = new TagEntity();
        CampanhaDTO campanhaDTO = getCampanhaDTOStatusEncerrada();

        tagEntity.setIdTag(null);
        tagEntity.setCampanhaEntities(null);
        tagEntity.setNomeTag(null);

        campanhaDTO.setDataLimite(LocalDateTime.now());

        when(usuarioService.getIdLoggedUser()).thenReturn(usuarioEntity.getIdUsuario());

        TagDTO tagDTO = new TagDTO();
        tagDTO.setNomeTag("oi");
        tagDTO.setIdTag(null);

        campanhaService.adicionar(campanhaDTO);

        assertNotNull(campanhaDTO);
        assertEquals(campanhaEntity.getIdCampanha(), campanhaDTO.getIdCampanha());
        assertEquals(campanhaEntity.getArrecadacao(), campanhaDTO.getArrecadacao());
        assertEquals(campanhaEntity.getMeta(), campanhaDTO.getMeta());
        assertEquals(campanhaEntity.getFotoCampanha(), campanhaDTO.getFotoCampanha());
        assertEquals(campanhaEntity.getEncerrarAutomaticamente(), campanhaDTO.getEncerrarAutomaticamente());
        assertEquals(campanhaEntity.getDataLimite(), campanhaDTO.getDataLimite());
        assertEquals(campanhaEntity.getStatusMeta(), campanhaDTO.getStatusMeta());
        assertEquals(campanhaEntity.getDescricao(), campanhaDTO.getDescricao());
        assertEquals(campanhaEntity.getTitulo(), campanhaDTO.getTitulo());
    }

    @Test
    public void deveTestarCreateSemSucessoSet() throws RegraDeNegocioException, CampanhaException {

        UsuarioEntity usuarioEntity = getUsuarioEntity();
        CampanhaEntity campanhaEntity = getCampanhaEntityEncerraAutomatico();

        TagEntity tagEntity = new TagEntity();
        tagEntity.setNomeTag("livros");

        CampanhaDTO campanhaDTO = getCampanhaDTOStatusEncerrada();

        TagCreateDTO tagCreateDTO = new TagCreateDTO();
        tagCreateDTO.setNomeTag("livros");

        Set<String> stringSet = Set.of("livros");

        campanhaDTO.setTags(stringSet);
        campanhaDTO.setArrecadacao(null);
        campanhaEntity.setUsuario(usuarioEntity);
        campanhaEntity.setIdUsuario(usuarioEntity.getIdUsuario());


        when(usuarioService.getLoggedUser()).thenReturn(usuarioEntity);
        when(tagService.adicionar(tagCreateDTO)).thenReturn(tagEntity);
        when(campanhaRepository.save(any(CampanhaEntity.class))).thenReturn(campanhaEntity);

        CampanhaDTO campanhaDTO2 = campanhaService.adicionar(campanhaDTO);

        assertNotNull(campanhaDTO2);
    }

    @Test
    public void deveTestarAdicionarFotoComSucesso() throws AmazonS3Exception, CampanhaException, IOException {
        CampanhaEntity campanhaEntity = getCampanhaEntityStatusEncerrada();

        final MultipartFile mockFile = mock(MultipartFile.class);

        when(campanhaRepository.findById(anyInt())).thenReturn(Optional.of(campanhaEntity));
        when(s3Service.uploadFile(mockFile)).thenReturn(URI.create("foto"));
        when(campanhaRepository.save(any(CampanhaEntity.class))).thenReturn(campanhaEntity);

        campanhaService.adicionarFoto(campanhaEntity.getIdUsuario(), mockFile);

        assertNotNull(mockFile);
    }

    @Test()
    public void deveTestarEditComSucesso() throws RegraDeNegocioException, CampanhaException {
        UsuarioEntity usuarioEntity = getUsuarioEntity();
        CampanhaEntity campanhaEntity = getCampanhaEntityNaoEncerraAutomatico();
        TagEntity tagEntity = new TagEntity();
        CampanhaDTO campanhaDTO = getCampanhaDTOStatusEncerrada();

        campanhaDTO.setArrecadacao(new BigDecimal(0));
        campanhaEntity.setArrecadacao(new BigDecimal(0));

        tagEntity.setNomeTag("livros");
        tagEntity.setIdTag(1);

        Set<String> tag = Set.of("livros");

        Set<TagEntity> tagEntitySet = Set.of(tagEntity);

        campanhaDTO.setTags(tag);

        campanhaEntity.setTagEntities(tagEntitySet);

        when(usuarioService.getLoggedUser()).thenReturn(usuarioEntity);
        when(tagService.findByNomeTag("livros")).thenReturn(Optional.of(tagEntity));
        when(campanhaRepository.save(any(CampanhaEntity.class))).thenReturn(campanhaEntity);
        when(campanhaRepository.findById(anyInt())).thenReturn(Optional.of(campanhaEntity));
        when(campanhaRepository.findAllByIdUsuarioAndIdCampanha(anyInt(), anyInt())).thenReturn(List.of(campanhaEntity));

        CampanhaDTO campanhaDTO2 = campanhaService.editar(1, campanhaDTO);

        assertNotNull(campanhaDTO2);
    }

    @Test
    public void deveTestarCreateSemSucessoSetEdit() throws RegraDeNegocioException, CampanhaException {

        UsuarioEntity usuarioEntity = getUsuarioEntity();
        CampanhaEntity campanhaEntity = getCampanhaEntityEncerraAutomatico();

        TagEntity tagEntity = new TagEntity();
        tagEntity.setNomeTag("livros");

        CampanhaDTO campanhaDTO = getCampanhaDTOStatusEncerrada();

        TagCreateDTO tagCreateDTO = new TagCreateDTO();
        tagCreateDTO.setNomeTag("livros");

        Set<String> stringSet = Set.of("livros");

        campanhaDTO.setTags(stringSet);
        campanhaDTO.setArrecadacao(null);
        campanhaEntity.setUsuario(usuarioEntity);
        campanhaEntity.setIdUsuario(usuarioEntity.getIdUsuario());
        campanhaEntity.setArrecadacao(new BigDecimal(0));

        when(usuarioService.getLoggedUser()).thenReturn(usuarioEntity);
        when(campanhaRepository.findById(anyInt())).thenReturn(Optional.of(campanhaEntity));
        when(campanhaRepository.findAllByIdUsuarioAndIdCampanha(anyInt(), anyInt())).thenReturn(List.of(campanhaEntity));
        when(usuarioService.getLoggedUser()).thenReturn(usuarioEntity);
        when(tagService.adicionar(tagCreateDTO)).thenReturn(tagEntity);
        when(campanhaRepository.save(any(CampanhaEntity.class))).thenReturn(campanhaEntity);

        CampanhaDTO campanhaDTO2 = campanhaService.editar(1, campanhaDTO);

        assertNotNull(campanhaDTO2);
    }

    @Test(expected = RegraDeNegocioException.class)
    public void deveTestarEditComException() throws RegraDeNegocioException, CampanhaException {
        CampanhaEntity campanhaEntity = getCampanhaEntityNaoEncerraAutomatico();
        CampanhaDTO campanhaDTO = getCampanhaDTOStatusEncerrada();

        campanhaDTO.setArrecadacao(new BigDecimal(95000));

        when(campanhaRepository.findById(anyInt())).thenReturn(Optional.of(campanhaEntity));

        campanhaService.editar(1, campanhaDTO);
    }

    @Test
    public void deveTestarLocalizarCampanha() throws CampanhaException {
        CampanhaEntity campanhaEntity = getCampanhaEntityEncerraAutomatico();
        Set<DoadorEntity> doadorEntitySet = Set.of(getDoadorEntity());
        campanhaEntity.setDoadores(doadorEntitySet);

        when(campanhaRepository.findById(anyInt())).thenReturn(Optional.of(campanhaEntity));

        CampanhaDTO campanhaDTO = campanhaService.localizarCampanha(campanhaEntity.getIdCampanha());

        assertNotNull(campanhaDTO);
    }

    @Test
    public void deveTestarListaDeCampanhasByUsuarioLogado() {
        UsuarioEntity usuarioEntity = getUsuarioEntity();

        when(campanhaRepository.findAllByIdUsuario(anyInt())).thenReturn(List.of(getCampanhaEntitySemDoacao()));
        when(usuarioService.getIdLoggedUser()).thenReturn(usuarioEntity.getIdUsuario());

        List<CampanhaDTO> campanhaDTOList = campanhaService.listaDeCampanhasByUsuarioLogado();
        assertNotNull(campanhaDTOList);
    }

    @Test
    public void deveTestarDeleteComSucesso() throws CampanhaException, RegraDeNegocioException {
        CampanhaEntity campanhaEntity = getCampanhaEntityEncerraAutomatico();

        when(campanhaRepository.findById(anyInt())).thenReturn(Optional.of(campanhaEntity));
        when(campanhaRepository.findById(anyInt())).thenReturn(Optional.of(campanhaEntity));
        when(campanhaRepository.findAllByIdUsuarioAndIdCampanha(anyInt(), anyInt())).thenReturn(List.of(campanhaEntity));

        campanhaService.deletar(campanhaEntity.getIdCampanha());
    }

    @Test
    public void deveTestarListarCampanhaComSucessoMetaAtingida() {
        TipoFiltro tipoFiltro = TipoFiltro.META_ATINGIDA;
        Boolean minhasContribuicoes = false, minhasCampanhas = false;
        List<Integer> idTags = List.of(1);

        List<CampanhaDTO> resultado = campanhaService.listarCampanha(tipoFiltro,minhasContribuicoes,minhasCampanhas,idTags);

        assertNotNull(resultado);
    }

    @Test
    public void deveTestarListarCampanhaComSucessoMetaNaoAtingida() {
        TipoFiltro tipoFiltro = TipoFiltro.META_NAO_ATINGIDA;
        Boolean minhasContribuicoes = false, minhasCampanhas = false;
        List<Integer> idTags = List.of(1);

        List<CampanhaDTO> resultado = campanhaService.listarCampanha(tipoFiltro,minhasContribuicoes,minhasCampanhas,idTags);

        assertNotNull(resultado);
    }

    @Test
    public void deveTestarListarCampanhaComSucessoTodas() {
        TipoFiltro tipoFiltro = TipoFiltro.TODAS;
        Boolean minhasContribuicoes = false, minhasCampanhas = false;
        List<Integer> idTags = List.of(1);

        List<CampanhaDTO> resultado = campanhaService.listarCampanha(tipoFiltro,minhasContribuicoes,minhasCampanhas,idTags);

        assertNotNull(resultado);
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
        campanhaEntity.setTagEntities(Set.of(getTagEntity()));
        campanhaEntity.setStatusMeta(false);
        return campanhaEntity;
    }

    public static CampanhaEntity getCampanhaEntitySemDoacao() {
        CampanhaEntity campanhaEntity = new CampanhaEntity();
        campanhaEntity.setIdCampanha(1);
        campanhaEntity.setFotoCampanha("foto_capa.jpeg");
        campanhaEntity.setArrecadacao(new BigDecimal(0));
        campanhaEntity.setTitulo("Livros usados");
        campanhaEntity.setDescricao("Doação de livros usados");
        campanhaEntity.setUltimaAlteracao(LocalDateTime.now());
        campanhaEntity.setMeta(new BigDecimal(2000.00));
        campanhaEntity.setUsuario(getUsuarioEntity());
        campanhaEntity.setDoadores(new HashSet<>());
        campanhaEntity.setIdUsuario(1);
        campanhaEntity.setDataLimite(LocalDateTime.of(2022, 9, 17, 23, 59, 59));
        campanhaEntity.setEncerrarAutomaticamente(false);
        campanhaEntity.setTagEntities(Set.of(getTagEntity()));
        campanhaEntity.setStatusMeta(false);
        return campanhaEntity;
    }

    public static CampanhaEntity getCampanhaEntityStatusEncerrada() {
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
        campanhaEntity.setStatusMeta(true);
        return campanhaEntity;
    }

    public static CampanhaDTO getCampanhaDTOStatusEncerrada() {
        CampanhaDTO campanhaDTO = new CampanhaDTO();
        campanhaDTO.setFotoCampanha("foto_capa.jpeg");
        campanhaDTO.setArrecadacao(new BigDecimal(200.00));
        campanhaDTO.setTitulo("Livros usados");
        campanhaDTO.setDescricao("Doação de livros usados");
        campanhaDTO.setUltimaAlteracao(LocalDateTime.now());
        campanhaDTO.setMeta(new BigDecimal(2000.00));
        campanhaDTO.setIdUsuario(1);
        campanhaDTO.setDataLimite(LocalDateTime.of(2022, 9, 17, 23, 59, 59));
        campanhaDTO.setEncerrarAutomaticamente(true);
        campanhaDTO.setStatusMeta(true);
        return campanhaDTO;
    }

    public static TagEntity getTagEntity() {
        TagEntity tagEntity = new TagEntity();
        tagEntity.setIdTag(1);
        tagEntity.setNomeTag("tagTeste1");
        tagEntity.setCampanhaEntities(Set.of(getCampanhaEntityEncerraAutomatico()));
        return tagEntity;
    }

    public static DoadorEntity getDoadorEntity() {
        DoadorEntity doadorEntity = new DoadorEntity();
        doadorEntity.setIdDoador(1);
        doadorEntity.setUsuario(getUsuarioEntity());
        doadorEntity.setValor(new BigDecimal(500));
        doadorEntity.setCampanhas(Set.of(getCampanhaEntityEncerraAutomatico()));
        return doadorEntity;
    }
}
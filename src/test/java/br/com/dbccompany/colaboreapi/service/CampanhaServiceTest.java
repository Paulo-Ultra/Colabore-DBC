package br.com.dbccompany.colaboreapi.service;

import br.com.dbccompany.colaboreapi.dto.campanha.CampanhaCreateDTO;
import br.com.dbccompany.colaboreapi.dto.campanha.CampanhaDTO;
import br.com.dbccompany.colaboreapi.dto.campanha.DoadorCampanhaDTO;
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
import com.amazonaws.services.s3.AmazonS3;
import br.com.dbccompany.colaboreapi.repository.UsuarioRepository;
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
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.util.ReflectionTestUtils;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.math.BigDecimal;
import java.net.MalformedURLException;
import java.net.URI;
import java.net.URL;
import java.net.URI;
import java.time.LocalDateTime;
import java.util.*;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.Mockito.*;

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

    @Mock
    private AmazonS3 amazonS3;
    private MockMultipartFile mockMultipartFile;
    private URL url;

    private final String bucketName = "";
    @Mock
    private TagService tagService;

    @Mock
    private UsuarioRepository usuarioRepository;

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
        TagEntity tagEntity = getTagEntity();
        CampanhaDTO campanhaDTO = getCampanhaDTOStatusEncerrada();
        campanhaDTO.setArrecadacao(null);

        when(usuarioService.getIdLoggedUser()).thenReturn(usuarioEntity.getIdUsuario());
        when(campanhaRepository.save(any(CampanhaEntity.class))).thenReturn(campanhaEntity);
        when(tagService.findById(anyInt())).thenReturn(tagEntity);
        CampanhaDTO campanhaDTO2 = campanhaService.adicionar(campanhaDTO);

        assertNotNull(campanhaDTO);
        assertEquals(campanhaEntity.getIdCampanha(), campanhaDTO2.getIdCampanha());
        assertEquals(campanhaEntity.getArrecadacao(), campanhaDTO2.getArrecadacao());
        assertEquals(campanhaEntity.getMeta(), campanhaDTO2.getMeta());
        assertEquals(campanhaEntity.getFotoCampanha(), campanhaDTO2.getFotoCampanha());
        assertEquals(campanhaEntity.getEncerrarAutomaticamente(), campanhaDTO2.getEncerrarAutomaticamente());
        assertEquals(campanhaEntity.getDataLimite(), campanhaDTO2.getDataLimite());
        assertEquals(campanhaEntity.getStatusMeta(), campanhaDTO2.getStatusMeta());
        assertEquals(campanhaEntity.getDescricao(), campanhaDTO2.getDescricao());
        assertEquals(campanhaEntity.getTitulo(), campanhaDTO2.getTitulo());
    }

    @Test(expected = RegraDeNegocioException.class)
    public void deveTestarCreateSemSucesso() throws RegraDeNegocioException, CampanhaException {
        UsuarioEntity usuarioEntity = getUsuarioEntity();
        CampanhaEntity campanhaEntity = getCampanhaEntityEncerraAutomatico();
        TagEntity tagEntity = getTagEntity();
        CampanhaDTO campanhaDTO = getCampanhaDTOStatusEncerrada();


        when(usuarioService.getIdLoggedUser()).thenReturn(usuarioEntity.getIdUsuario());
        when(campanhaRepository.save(any(CampanhaEntity.class))).thenReturn(campanhaEntity);
        when(tagService.findById(anyInt())).thenReturn(tagEntity);

        campanhaDTO.setMeta(new BigDecimal(0));
        campanhaDTO.setArrecadacao(new BigDecimal(50));

        CampanhaDTO campanhaDTO2 = campanhaService.adicionar(campanhaDTO);

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

    @Test(expected = RegraDeNegocioException.class)
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
        when(campanhaRepository.save(any(CampanhaEntity.class))).thenReturn(campanhaEntity);
        when(tagService.findById(anyInt())).thenReturn(tagEntity);

        TagDTO tagDTO = new TagDTO();
        tagDTO.setNomeTag("oi");
        tagDTO.setIdTag(null);
        Set<TagDTO> tagDTOSet = Set.of(tagDTO);
        campanhaDTO.setTags(tagDTOSet);

        CampanhaDTO campanhaDTO2 = campanhaService.adicionar(campanhaDTO);

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

    @Test(expected = RegraDeNegocioException.class)
    public void deveTestarCreateSemSucessoSet() throws RegraDeNegocioException, CampanhaException {
        UsuarioEntity usuarioEntity = getUsuarioEntity();
        CampanhaEntity campanhaEntity = getCampanhaEntityEncerraAutomatico();
        TagEntity tagEntity = new TagEntity();
        CampanhaDTO campanhaDTO = getCampanhaDTOStatusEncerrada();

        tagEntity.setIdTag(null);
        tagEntity.setCampanhaEntities(null);
        tagEntity.setNomeTag(null);

        when(usuarioService.getIdLoggedUser()).thenReturn(usuarioEntity.getIdUsuario());
        when(campanhaRepository.save(any(CampanhaEntity.class))).thenReturn(campanhaEntity);
        when(tagService.findById(anyInt())).thenReturn(tagEntity);

        TagDTO tagDTO = new TagDTO();
        tagDTO.setNomeTag("oi");
        tagDTO.setIdTag(null);
        Set<TagDTO> tagDTOSet = Set.of(tagDTO);
        campanhaDTO.setTags(tagDTOSet);

        CampanhaDTO campanhaDTO2 = campanhaService.adicionar(campanhaDTO);

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
        CampanhaEntity campanhaEntity = getCampanhaEntitySemDoacao();
        TagEntity tagEntity = new TagEntity();


        when(usuarioService.getLoggedUser()).thenReturn(usuarioEntity);
        when(campanhaRepository.save(any(CampanhaEntity.class))).thenReturn(campanhaEntity);
        when(campanhaRepository.findById(anyInt())).thenReturn(Optional.of(campanhaEntity));
        when(campanhaRepository.findAllByIdUsuarioAndIdCampanha(anyInt(), anyInt())).thenReturn(List.of(campanhaEntity));
        when(tagService.findById(anyInt())).thenReturn(tagEntity);

        CampanhaDTO campanhaDTO = campanhaService.editar(1, getCampanhaCreateDTOStatusEncerrada());

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

    @Test(expected = RegraDeNegocioException.class)
    public void deveTestarEditComException() throws RegraDeNegocioException, CampanhaException {
        UsuarioEntity usuarioEntity = getUsuarioEntity();
        CampanhaEntity campanhaEntity = getCampanhaEntityNaoEncerraAutomatico();
        TagEntity tagEntity = new TagEntity();


        when(usuarioService.getLoggedUser()).thenReturn(usuarioEntity);
        when(campanhaRepository.save(any(CampanhaEntity.class))).thenReturn(campanhaEntity);
        when(campanhaRepository.findById(anyInt())).thenReturn(Optional.of(campanhaEntity));
        when(tagService.findById(anyInt())).thenReturn(tagEntity);

        campanhaService.editar(1, getCampanhaCreateDTOStatusEncerrada());
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
        when(usuarioService.getLoggedUser()).thenReturn(getUsuarioEntity());
        when(campanhaRepository.save(any(CampanhaEntity.class))).thenReturn(campanhaEntity);
        when(campanhaRepository.findById(anyInt())).thenReturn(Optional.of(campanhaEntity));
        when(campanhaRepository.findAllByIdUsuarioAndIdCampanha(anyInt(), anyInt())).thenReturn(List.of(campanhaEntity));
        campanhaService.deletar(campanhaEntity.getIdCampanha());
    }

    @Test
    public void deveTestarListarCampanhaComSucessoMetaAtingida() {
        TipoFiltro tipoFiltro = TipoFiltro.META_ATINGIDA;
        Boolean minhasContribuicoes = false, minhasCampanhas = false;
        List<Integer> idTags = List.of(1);
        Integer idUsuario = 1;
        List<CampanhaEntity> campanhaEntityList = List.of(getCampanhaEntityNaoEncerraAutomatico());

        if (tipoFiltro.equals(TipoFiltro.META_ATINGIDA)) {
            when(campanhaRepository.findAll(false, idUsuario, idTags, minhasContribuicoes, minhasCampanhas)).thenReturn(campanhaEntityList);
        }

        List<CampanhaDTO> resultado = campanhaService.listarCampanha(tipoFiltro,minhasContribuicoes,minhasCampanhas,idTags);

        assertNotNull(resultado);
    }

    @Test
    public void deveTestarListarCampanhaComSucessoMetaNaoAtingida() {
        TipoFiltro tipoFiltro = TipoFiltro.META_NAO_ATINGIDA;
        Boolean minhasContribuicoes = false, minhasCampanhas = false;
        List<Integer> idTags = List.of(1);
        Integer idUsuario = 1;
        List<CampanhaEntity> campanhaEntityList = List.of(getCampanhaEntityNaoEncerraAutomatico());

        if (tipoFiltro.equals(TipoFiltro.META_NAO_ATINGIDA)) {
            when(campanhaRepository.findAll(false, idUsuario, idTags, minhasContribuicoes, minhasCampanhas)).thenReturn(campanhaEntityList);
        }

        List<CampanhaDTO> resultado = campanhaService.listarCampanha(tipoFiltro,minhasContribuicoes,minhasCampanhas,idTags);

        assertNotNull(resultado);
    }

    @Test
    public void deveTestarListarCampanhaComSucessoTodas() {
        TipoFiltro tipoFiltro = TipoFiltro.TODAS;
        Boolean minhasContribuicoes = false, minhasCampanhas = false;
        List<Integer> idTags = List.of(1);
        Integer idUsuario = 1;
        DoadorEntity doadorEntity = getDoadorEntity();
        List<CampanhaEntity> campanhaEntityList = List.of(getCampanhaEntityNaoEncerraAutomatico());


        if (tipoFiltro.equals(TipoFiltro.TODAS)) {
            when(campanhaRepository.findAll(false, idUsuario, idTags, minhasContribuicoes, minhasCampanhas)).thenReturn(campanhaEntityList);
        }

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

    public static CampanhaCreateDTO getCampanhaCreateDTOStatusEncerrada() {
        CampanhaCreateDTO campanhaCreateDTO = new CampanhaCreateDTO();
        campanhaCreateDTO.setFotoCampanha("foto_capa.jpeg");
        campanhaCreateDTO.setArrecadacao(new BigDecimal(200.00));
        campanhaCreateDTO.setTitulo("Livros usados");
        campanhaCreateDTO.setDescricao("Doação de livros usados");
        campanhaCreateDTO.setUltimaAlteracao(LocalDateTime.now());
        campanhaCreateDTO.setMeta(new BigDecimal(2000.00));
        campanhaCreateDTO.setIdUsuario(1);
        campanhaCreateDTO.setDataLimite(LocalDateTime.of(2022, 9, 17, 23, 59, 59));
        campanhaCreateDTO.setEncerrarAutomaticamente(true);
        campanhaCreateDTO.setStatusMeta(true);
        return campanhaCreateDTO;
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
        campanhaDTO.setTags(Set.of(getTagDTO()));
        return campanhaDTO;
    }

    public static TagEntity getTagEntity() {
        TagEntity tagEntity = new TagEntity();
        tagEntity.setIdTag(1);
        tagEntity.setNomeTag("tagTeste1");
        tagEntity.setCampanhaEntities(Set.of(getCampanhaEntityEncerraAutomatico()));
        return tagEntity;
    }

    public static TagDTO getTagDTO(){
        TagDTO tagDTO = new TagDTO();
        tagDTO.setIdTag(1);
        tagDTO.setNomeTag("tagTeste1");
        return tagDTO;
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
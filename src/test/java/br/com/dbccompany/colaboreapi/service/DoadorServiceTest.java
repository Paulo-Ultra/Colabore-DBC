package br.com.dbccompany.colaboreapi.service;

import br.com.dbccompany.colaboreapi.dto.doador.DoadorCreateDTO;
import br.com.dbccompany.colaboreapi.dto.doador.DoadorDTO;
import br.com.dbccompany.colaboreapi.entity.CampanhaEntity;
import br.com.dbccompany.colaboreapi.entity.DoadorEntity;
import br.com.dbccompany.colaboreapi.entity.TagEntity;
import br.com.dbccompany.colaboreapi.entity.UsuarioEntity;
import br.com.dbccompany.colaboreapi.exceptions.CampanhaException;
import br.com.dbccompany.colaboreapi.exceptions.DoacaoException;
import br.com.dbccompany.colaboreapi.exceptions.RegraDeNegocioException;
import br.com.dbccompany.colaboreapi.repository.CampanhaRepository;
import br.com.dbccompany.colaboreapi.repository.DoadorRepository;
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
import java.util.HashSet;
import java.util.Set;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.Mockito.any;
import static org.mockito.Mockito.when;

@ContextConfiguration(classes = {DoadorService.class})
@RunWith(MockitoJUnitRunner.class)
public class DoadorServiceTest {

    @InjectMocks
    private DoadorService doadorService;

    private ObjectMapper objectMapper = new ObjectMapper();

    @Mock
    private UsuarioService usuarioService;

    @Mock
    private CampanhaService campanhaService;

    @Mock
    private CampanhaRepository campanhaRepository;

    @Mock
    private DoadorRepository doadorRepository;

    @Before
    public void init() {
        objectMapper.registerModule(new JavaTimeModule());
        objectMapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
        objectMapper.configure(SerializationFeature.FAIL_ON_EMPTY_BEANS, false);
        objectMapper.disable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS);
        ReflectionTestUtils.setField(doadorService, "objectMapper", objectMapper);
    }

    @Test
    public void deveTestarAdicionarComSucesso() throws CampanhaException, RegraDeNegocioException, DoacaoException {
        UsuarioEntity usuarioEntity = getUsuarioEntity();
        DoadorCreateDTO doadorCreateDTO = getDoadorCreateDTO();
        DoadorEntity doadorEntity = getDoadorEntity();
        CampanhaEntity campanhaEntity = getCampanhaEntityNaoEncerraAutomatico();
        Set<CampanhaEntity> campanhaEntitySet = Set.of(campanhaEntity);

        doadorEntity.setCampanhas(campanhaEntitySet);

        when(usuarioService.getLoggedUser()).thenReturn(usuarioEntity);
        when(campanhaService.buscarIdCampanha(anyInt())).thenReturn(campanhaEntity);
        when(campanhaRepository.save(any(CampanhaEntity.class))).thenReturn(campanhaEntity);
        when(doadorRepository.save(any(DoadorEntity.class))).thenReturn(doadorEntity);

        DoadorDTO doadorDTO = doadorService.adicionar(campanhaEntity.getIdCampanha(), doadorCreateDTO);

        assertNotNull(doadorDTO);
        assertEquals(new BigDecimal(500), doadorDTO.getValor());
        assertEquals(doadorEntity.getIdUsuario(), doadorDTO.getIdUsuario());
    }

    @Test(expected = DoacaoException.class)
    public void deveTestarAdicionarSemSucesso() throws CampanhaException, RegraDeNegocioException, DoacaoException {
        UsuarioEntity usuarioEntity = getUsuarioEntity();
        DoadorCreateDTO doadorCreateDTO = getDoadorCreateDTO();
        DoadorEntity doadorEntity = getDoadorEntity();
        CampanhaEntity campanhaEntity = getCampanhaEntityEncerraAutomatico();
        Set<CampanhaEntity> campanhaEntitySet = Set.of(campanhaEntity);

        doadorEntity.setCampanhas(campanhaEntitySet);

        campanhaEntity.setStatusMeta(true);

        when(usuarioService.getLoggedUser()).thenReturn(usuarioEntity);
        when(campanhaService.buscarIdCampanha(anyInt())).thenReturn(campanhaEntity);

        DoadorDTO doadorDTO = doadorService.adicionar(campanhaEntity.getIdCampanha(), doadorCreateDTO);
        //FIXME Retirar asserts
        assertNotNull(doadorDTO);
        assertEquals(new BigDecimal(500), doadorDTO.getValor());
        assertEquals(doadorEntity.getIdUsuario(), doadorDTO.getIdUsuario());
    }

    @Test(expected = DoacaoException.class)
    public void deveTestarAdicionarSemSucessoDataLimite() throws CampanhaException, RegraDeNegocioException, DoacaoException {
        UsuarioEntity usuarioEntity = getUsuarioEntity();
        DoadorCreateDTO doadorCreateDTO = getDoadorCreateDTO();
        DoadorEntity doadorEntity = getDoadorEntity();
        CampanhaEntity campanhaEntity = getCampanhaEntityEncerraAutomatico();
        Set<CampanhaEntity> campanhaEntitySet = Set.of(campanhaEntity);

        doadorEntity.setCampanhas(campanhaEntitySet);

        campanhaEntity.setStatusMeta(false);
        campanhaEntity.setDataLimite(LocalDateTime.parse("2021-08-17T00:00:00"));

        when(usuarioService.getLoggedUser()).thenReturn(usuarioEntity);
        when(campanhaService.buscarIdCampanha(anyInt())).thenReturn(campanhaEntity);

        doadorService.adicionar(campanhaEntity.getIdCampanha(), doadorCreateDTO);
    }

    @Test(expected = DoacaoException.class)
    public void deveTestarAdicionarSemSucessoMesmoCriador() throws CampanhaException, RegraDeNegocioException, DoacaoException {
        UsuarioEntity usuarioEntity = getUsuarioEntity();
        DoadorCreateDTO doadorCreateDTO = getDoadorCreateDTO();
        DoadorEntity doadorEntity = getDoadorEntity();
        CampanhaEntity campanhaEntity = getCampanhaEntityEncerraAutomatico();
        Set<CampanhaEntity> campanhaEntitySet = Set.of(campanhaEntity);

        doadorEntity.setCampanhas(campanhaEntitySet);

        when(usuarioService.getLoggedUser()).thenReturn(usuarioEntity);
        when(usuarioService.getIdLoggedUser()).thenReturn(1);
        when(campanhaService.buscarIdCampanha(anyInt())).thenReturn(campanhaEntity);

        doadorService.adicionar(campanhaEntity.getIdCampanha(), doadorCreateDTO);
    }

    @Test
    public void deveTestarAdicionarComSucessoCompararIf() throws CampanhaException, RegraDeNegocioException, DoacaoException {
        UsuarioEntity usuarioEntity = getUsuarioEntity();
        DoadorCreateDTO doadorCreateDTO = getDoadorCreateDTO();
        DoadorEntity doadorEntity = getDoadorEntity();
        CampanhaEntity campanhaEntity = getCampanhaEntityNaoEncerraAutomatico();
        Set<CampanhaEntity> campanhaEntitySet = Set.of(campanhaEntity);

        doadorEntity.setCampanhas(campanhaEntitySet);
        doadorCreateDTO.setValor(new BigDecimal(950000));
        campanhaEntity.setStatusMeta(false);
        campanhaEntity.setArrecadacao(new BigDecimal(50));
        campanhaEntity.setMeta(new BigDecimal(40));

        when(usuarioService.getLoggedUser()).thenReturn(usuarioEntity);
        when(campanhaService.buscarIdCampanha(anyInt())).thenReturn(campanhaEntity);
        when(campanhaRepository.save(any(CampanhaEntity.class))).thenReturn(campanhaEntity);
        when(doadorRepository.save(any(DoadorEntity.class))).thenReturn(doadorEntity);

        DoadorDTO doadorDTO = doadorService.adicionar(campanhaEntity.getIdCampanha(), doadorCreateDTO);

        assertNotNull(doadorDTO);
    }

    @Test
    public void deveTestarAdicionarComSucessoCompararElse() throws CampanhaException, RegraDeNegocioException, DoacaoException {
        UsuarioEntity usuarioEntity = getUsuarioEntity();
        DoadorCreateDTO doadorCreateDTO = getDoadorCreateDTO();
        DoadorEntity doadorEntity = getDoadorEntity();
        CampanhaEntity campanhaEntity = getCampanhaEntityNaoEncerraAutomatico();
        Set<CampanhaEntity> campanhaEntitySet = Set.of(campanhaEntity);

        doadorEntity.setCampanhas(campanhaEntitySet);
        doadorCreateDTO.setValor(new BigDecimal(30));
        campanhaEntity.setStatusMeta(false);
        campanhaEntity.setArrecadacao(new BigDecimal(50));
        campanhaEntity.setMeta(new BigDecimal(40));

        when(usuarioService.getLoggedUser()).thenReturn(usuarioEntity);
        when(campanhaService.buscarIdCampanha(anyInt())).thenReturn(campanhaEntity);
        when(campanhaRepository.save(any(CampanhaEntity.class))).thenReturn(campanhaEntity);
        when(doadorRepository.save(any(DoadorEntity.class))).thenReturn(doadorEntity);

        DoadorDTO doadorDTO = doadorService.adicionar(campanhaEntity.getIdCampanha(), doadorCreateDTO);

        assertNotNull(doadorDTO);
    }

    public static DoadorCreateDTO getDoadorCreateDTO() {
        DoadorCreateDTO doadorCreateDTO = new DoadorCreateDTO();
        doadorCreateDTO.setIdUsuario(1);
        doadorCreateDTO.setValor(new BigDecimal(500));
        return doadorCreateDTO;
    }

    public static DoadorEntity getDoadorEntity() {
        DoadorEntity doadorEntity = new DoadorEntity();
        doadorEntity.setIdDoador(1);
        doadorEntity.setUsuario(getUsuarioEntity());
        doadorEntity.setIdUsuario(1);
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
        campanhaEntity.setTagEntities(Set.of(getTagEntity()));
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
    public static TagEntity getTagEntity() {
        TagEntity tagEntity = new TagEntity();
        tagEntity.setIdTag(1);
        tagEntity.setNomeTag("tagTeste1");
        tagEntity.setCampanhaEntities(Set.of(getCampanhaEntityEncerraAutomatico()));
        return tagEntity;
    }
}

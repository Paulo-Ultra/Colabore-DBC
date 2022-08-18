package br.com.dbccompany.colaboreapi.service;

import br.com.dbccompany.colaboreapi.dto.campanha.CampanhaCreateDTO;
import br.com.dbccompany.colaboreapi.dto.campanha.CampanhaDTO;
import br.com.dbccompany.colaboreapi.entity.CampanhaEntity;
import br.com.dbccompany.colaboreapi.entity.UsuarioEntity;
import br.com.dbccompany.colaboreapi.exceptions.AmazonS3Exception;
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
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.util.ReflectionTestUtils;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.math.BigDecimal;
import java.net.URI;
import java.net.URISyntaxException;
import java.time.LocalDateTime;
import java.util.HashSet;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.multipart;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

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
    private WebApplicationContext webApplicationContext;

    private ObjectMapper objectMapper = new ObjectMapper();

    @Before
    public void init() {
        objectMapper.registerModule(new JavaTimeModule());
        objectMapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
        objectMapper.configure(SerializationFeature.FAIL_ON_EMPTY_BEANS, false);
        objectMapper.disable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS);
        ReflectionTestUtils.setField(campanhaService, "objectMapper", objectMapper);
    }

    @Test
    public void deveTestarCreateComSucesso () throws RegraDeNegocioException {
        UsuarioEntity usuarioEntity = getUsuarioEntity();
        CampanhaEntity campanhaEntity = getCampanhaEntityEncerraAutomatico();


        when(usuarioService.getIdLoggedUser()).thenReturn(usuarioEntity.getIdUsuario());
        when(campanhaRepository.save(any(CampanhaEntity.class))).thenReturn(campanhaEntity);

        CampanhaDTO campanhaDTO = campanhaService.adicionar(getCampanhaCreateDTOStatusEncerrada());

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
    public void deveTestarAdicionarFotoComSucesso() throws RegraDeNegocioException, AmazonS3Exception, URISyntaxException {
        CampanhaEntity campanhaEntity = getCampanhaEntityStatusEncerrada();

        when(campanhaService.localizarCampanha(anyInt())).thenReturn(campanhaEntity);
//        when(s3Service.uploadFile(any(MultipartFile.class))).thenReturn(getURI());
        when(campanhaRepository.save(any(CampanhaEntity.class))).thenReturn(campanhaEntity);

//        CampanhaDTO campanhaDTO = campanhaService.adicionarFoto(getCampanhaEntityEncerraAutomatico().getIdCampanha(), );

    }

    public static UsuarioEntity getUsuarioEntity(){
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

    public static CampanhaEntity getCampanhaEntityEncerraAutomatico(){
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
        campanhaEntity.setTagEntities(new HashSet<>());
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

//    @Test
//    public void whenFileUploaded_thenVerifyStatus()
//            throws Exception {
//        MockMultipartFile file
//                = new MockMultipartFile(
//                "file",
//                "hello.txt",
//                MediaType.TEXT_PLAIN_VALUE,
//                "Hello, World!".getBytes()
//        );
//
//        MockMvc mockMvc
//                = MockMvcBuilders.webAppContextSetup(webApplicationContext).build();
//        mockMvc.perform(multipart("/upload").file(file))
//                .andExpect(status().isOk());
//    }
//

    @Test
    public void test() throws Exception {

        MockMultipartFile firstFile = new MockMultipartFile("data", "filename.txt", "text/plain", "some xml".getBytes());
        MockMultipartFile secondFile = new MockMultipartFile("data", "other-file-name.data", "text/plain", "some other type".getBytes());
        MockMultipartFile jsonFile = new MockMultipartFile("json", "", "application/json", "{\"json\": \"someValue\"}".getBytes());

        MockMvc mockMvc = MockMvcBuilders.webAppContextSetup(webApplicationContext).build();
        mockMvc.perform(MockMvcRequestBuilders.multipart("/upload")
                        .file(firstFile)
                        .file(secondFile)
                        .file(jsonFile)
                        .param("some-random", "4"))
                .andExpect(status().is(200))
                .andExpect(content().string("success"));
    }

}

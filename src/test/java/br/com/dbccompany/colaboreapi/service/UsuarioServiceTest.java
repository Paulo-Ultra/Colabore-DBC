package br.com.dbccompany.colaboreapi.service;

import br.com.dbccompany.colaboreapi.dto.usuario.UsuarioCreateDTO;
import br.com.dbccompany.colaboreapi.dto.usuario.UsuarioDTO;
import br.com.dbccompany.colaboreapi.dto.usuario.UsuarioSemSenhaDTO;
import br.com.dbccompany.colaboreapi.entity.UsuarioEntity;
import br.com.dbccompany.colaboreapi.exceptions.AmazonS3Exception;
import br.com.dbccompany.colaboreapi.exceptions.RegraDeNegocioException;
import br.com.dbccompany.colaboreapi.repository.CampanhaRepository;
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
import java.net.MalformedURLException;
import java.net.URI;
import java.net.URL;
import java.util.HashSet;
import java.util.Optional;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.Mockito.*;

@ContextConfiguration(classes = {UsuarioService.class})
@RunWith(MockitoJUnitRunner.class)
public class UsuarioServiceTest {

    @InjectMocks
    private UsuarioService usuarioService;
    @Mock
    private UsuarioRepository usuarioRepository;
    @Mock
    private S3Service s3Service;
    @Mock
    private CampanhaRepository campanhaRepository;
    private ObjectMapper objectMapper = new ObjectMapper();

    private MockMultipartFile mockMultipartFile;
    private URL url;

    private final String bucketName = "";


    @Before
    public void init() throws MalformedURLException {
        objectMapper.registerModule(new JavaTimeModule());
        objectMapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
        objectMapper.configure(SerializationFeature.FAIL_ON_EMPTY_BEANS, false);
        objectMapper.disable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS);
        ReflectionTestUtils.setField(usuarioService, "objectMapper", objectMapper);

        mockMultipartFile = null;

        url = new URL(
                "https",
                "stackoverflow.com",
                80, "pages/page1.html");
    }


    @Test
    public void deveTestarAdicionarFotoNull() throws AmazonS3Exception, IOException, RegraDeNegocioException {

        UsuarioEntity usuarioEntity = getUsuarioEntity();
        deveTestarGetIdLoggedUser();

        final MultipartFile mockFile = mock(MultipartFile.class);

        when(usuarioRepository.findById(anyInt())).thenReturn(Optional.of(usuarioEntity));
        when(usuarioRepository.save(any(UsuarioEntity.class))).thenReturn(usuarioEntity);

        usuarioService.adicionarFoto(null);

        assertNotNull(mockFile);

    }
    @Test
    public void deveTestarAdicionarComSucesso() throws RegraDeNegocioException {
        UsuarioEntity usuarioEntity = getUsuarioEntity();

        when(usuarioRepository.save(any(UsuarioEntity.class))).thenReturn(usuarioEntity);

        UsuarioDTO usuarioDTO = usuarioService.adicionar(getUsuarioCreateDTO());

        assertNotNull(usuarioDTO);
        assertEquals(usuarioEntity.getNome(), usuarioDTO.getNome());
        assertEquals(usuarioEntity.getEmail(), usuarioDTO.getEmail());
        assertEquals(usuarioEntity.getSenha(), usuarioDTO.getSenha());
    }

    @Test(expected = RegraDeNegocioException.class)
    public void deveTestarAdicionarComException() throws RegraDeNegocioException {
        UsuarioEntity usuarioEntity = getUsuarioEntity();

        usuarioService.adicionar(getUsuarioCreateDTOEmailIncorreto());
    }

    @Test
    public void deveTestarAdicionarFotoComSucesso() throws RegraDeNegocioException, AmazonS3Exception, IOException {
        UsuarioEntity usuarioEntity = new UsuarioEntity();
        deveTestarGetIdLoggedUser();
        usuarioEntity = getUsuarioEntity();

        final MultipartFile mockFile = mock(MultipartFile.class);

        when(usuarioRepository.findById(anyInt())).thenReturn(Optional.of(usuarioEntity));
        when(s3Service.uploadFile(mockFile)).thenReturn(URI.create("foto"));
        when(usuarioRepository.save(any(UsuarioEntity.class))).thenReturn(usuarioEntity);

        usuarioService.adicionarFoto(mockFile);

        assertNotNull(mockFile);

    }

    @Test
    public void deveTestarFindByEmail() {
        UsuarioEntity usuarioEntity = getUsuarioEntity();

        when(usuarioRepository.findByEmail(anyString())).thenReturn(Optional.of(usuarioEntity));

        usuarioService.findByEmail(usuarioEntity.getEmail());

        assertNotNull(usuarioEntity);
    }

    @Test
    public void deveTestarGetIdLoggedUser() {
        UsernamePasswordAuthenticationToken usernamePasswordAuthenticationToken =
                new UsernamePasswordAuthenticationToken(
                        123,
                        "senha"
                );
        SecurityContextHolder.getContext().setAuthentication(usernamePasswordAuthenticationToken);

        Integer idLoggedUser = usuarioService.getIdLoggedUser();
        assertEquals(123, idLoggedUser.intValue());
    }
    @Test
    public void deveTestarGetLoggedUser() throws RegraDeNegocioException {
        UsuarioEntity usuarioEntity = getUsuarioEntity();
        deveTestarGetIdLoggedUser();

        when(usuarioRepository.findById(anyInt())).thenReturn(Optional.of(usuarioEntity));

        usuarioService.getLoggedUser();

        assertNotNull(usuarioEntity);
        assertEquals("teste@dbccompany.com.br", usuarioEntity.getEmail());
        assertEquals("Teste", usuarioEntity.getNome());
    }

    @Test
    public void deveTestarRetornarUsuarioLogado() throws RegraDeNegocioException {
        UsuarioEntity usuarioEntity = getUsuarioEntity();
        deveTestarGetLoggedUser();

        when(usuarioRepository.findById(anyInt())).thenReturn(Optional.of(usuarioEntity));

        UsuarioSemSenhaDTO usuarioSemSenhaDTO = usuarioService.retornarUsuarioLogado();

        assertNotNull(usuarioSemSenhaDTO);
    }

    @Test
    public void deveTestarFindLoginById() throws RegraDeNegocioException {
        UsuarioEntity usuarioEntity = getUsuarioEntity();
        deveTestarGetIdLoggedUser();

        when(usuarioRepository.findById(anyInt())).thenReturn(Optional.of(usuarioEntity));

        UsuarioDTO usuarioDTO = usuarioService.findLoginById(usuarioService.getIdLoggedUser());

        assertNotNull(usuarioDTO);
    }

    @Test(expected = RegraDeNegocioException.class)
    public void deveTestarFindLoginByIdComException() throws RegraDeNegocioException {
        UsuarioEntity usuarioEntity = getUsuarioEntity();
        deveTestarGetIdLoggedUser();
        usuarioEntity.setIdUsuario(99);

        UsuarioDTO usuarioDTO = usuarioService.findLoginById(usuarioEntity.getIdUsuario());
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

        public static UsuarioCreateDTO getUsuarioCreateDTO() {
            UsuarioCreateDTO usuarioCreateDTO = new UsuarioCreateDTO();
            usuarioCreateDTO.setNome("Teste");
            usuarioCreateDTO.setSenha("123");
            usuarioCreateDTO.setEmail("teste@dbccompany.com.br");
            return usuarioCreateDTO;
    }

    public static UsuarioCreateDTO getUsuarioCreateDTOEmailIncorreto() {
        UsuarioCreateDTO usuarioCreateDTO = new UsuarioCreateDTO();
        usuarioCreateDTO.setNome("Teste");
        usuarioCreateDTO.setSenha("123");
        usuarioCreateDTO.setEmail("teste@gmail.com");
        return usuarioCreateDTO;
    }
}

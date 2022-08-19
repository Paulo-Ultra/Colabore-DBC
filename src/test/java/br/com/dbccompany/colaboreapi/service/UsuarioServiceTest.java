package br.com.dbccompany.colaboreapi.service;

import br.com.dbccompany.colaboreapi.dto.campanha.CampanhaCreateDTO;
import br.com.dbccompany.colaboreapi.dto.campanha.CampanhaDTO;
import br.com.dbccompany.colaboreapi.dto.tag.TagDTO;
import br.com.dbccompany.colaboreapi.dto.usuario.UsuarioCreateDTO;
import br.com.dbccompany.colaboreapi.dto.usuario.UsuarioDTO;
import br.com.dbccompany.colaboreapi.dto.usuario.UsuarioSemSenhaDTO;
import br.com.dbccompany.colaboreapi.entity.CampanhaEntity;
import br.com.dbccompany.colaboreapi.entity.DoadorEntity;
import br.com.dbccompany.colaboreapi.entity.TagEntity;
import br.com.dbccompany.colaboreapi.entity.UsuarioEntity;
import br.com.dbccompany.colaboreapi.exceptions.CampanhaException;
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
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.util.ReflectionTestUtils;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.*;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.Mockito.*;

@ContextConfiguration(classes = {CampanhaService.class})
@RunWith(MockitoJUnitRunner.class)
public class UsuarioServiceTest {

    @InjectMocks
    private UsuarioService usuarioService;
    @Mock
    private UsuarioRepository usuarioRepository;
    private ObjectMapper objectMapper = new ObjectMapper();

    @Before
    public void init() {
        objectMapper.registerModule(new JavaTimeModule());
        objectMapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
        objectMapper.configure(SerializationFeature.FAIL_ON_EMPTY_BEANS, false);
        objectMapper.disable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS);
        ReflectionTestUtils.setField(usuarioService, "objectMapper", objectMapper);
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

//    @Test(expected = RegraDeNegocioException.class)
//    public void deveTestarGetLoggedUserComException() throws RegraDeNegocioException {
//        UsuarioEntity usuarioEntity = getUsuarioEntity();
//        deveTestarGetIdLoggedUser();
//        usuarioEntity.setIdUsuario(100);
//
//        when(usuarioRepository.findById(anyInt())).thenReturn(Optional.of(usuarioEntity));
//
//        usuarioService.getLoggedUser();
//
//        assertNotNull(usuarioEntity);
//        assertEquals("teste@dbccompany.com.br", usuarioEntity.getEmail());
//        assertEquals("Teste", usuarioEntity.getNome());
//    }

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
    public void deveTestarFindLoginByIdComRxception() throws RegraDeNegocioException {
        UsuarioEntity usuarioEntity = getUsuarioEntity();
        deveTestarGetIdLoggedUser();


        when(usuarioRepository.findById(anyInt())).thenReturn(Optional.of(usuarioEntity));

        UsuarioDTO usuarioDTO = usuarioService.findLoginById(usuarioEntity.getIdUsuario());

        assertNotNull(usuarioDTO);
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

    public static UsuarioSemSenhaDTO getUsuarioSemSenhaDTO() {
        UsuarioSemSenhaDTO usuarioSemSenhaDTO = new UsuarioSemSenhaDTO();
        usuarioSemSenhaDTO.setNome("Teste");
        usuarioSemSenhaDTO.setEmail("teste@gmail.com");
        usuarioSemSenhaDTO.setIdUsuario(1);
        usuarioSemSenhaDTO.setFoto("teste.jpg");
        return usuarioSemSenhaDTO;
    }

}

package br.com.dbccompany.colaboreapi.service;

import br.com.dbccompany.colaboreapi.dto.campanha.CampanhaCreateDTO;
import br.com.dbccompany.colaboreapi.dto.campanha.CampanhaDTO;
import br.com.dbccompany.colaboreapi.entity.CampanhaEntity;
import br.com.dbccompany.colaboreapi.entity.UsuarioEntity;
import br.com.dbccompany.colaboreapi.exceptions.CampanhaNaoEncontradaException;
import br.com.dbccompany.colaboreapi.exceptions.RegraDeNegocioException;
import br.com.dbccompany.colaboreapi.repository.CampanhaRepository;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class CampanhaService {

    private final ObjectMapper objectMapper;
    private final UsuarioService usuarioService;
    private final CampanhaRepository campanhaRepository;

    public CampanhaDTO adicionar(CampanhaCreateDTO campanhaCreateDTO) throws RegraDeNegocioException {

        Integer id = usuarioService.idUsuarioLogado();

        UsuarioEntity usuarioRecuperado = usuarioService.localizarUsuario(id);

        CampanhaEntity campanhaEntity = retornarEntity(campanhaCreateDTO);

        campanhaEntity.setUsuario(usuarioRecuperado);
        campanhaEntity.setIdUsuario(usuarioRecuperado.getIdUsuario());

        if (campanhaCreateDTO.getArrecadacao() == null) {
            campanhaEntity.setArrecadacao(BigDecimal.valueOf(0));
        }

        return retornarDTO(campanhaRepository.save(campanhaEntity));
    }

    public CampanhaDTO editar(Integer id,
                             CampanhaCreateDTO campanhaDTO) throws RegraDeNegocioException, CampanhaNaoEncontradaException {

        CampanhaEntity campanhaRecuperada = buscarIdCampanha(id);

        Integer idUsuario = usuarioService.idUsuarioLogado();

        UsuarioEntity usuarioCampanha = usuarioService.localizarUsuario(idUsuario);

        campanhaRecuperada.setMeta(campanhaDTO.getMeta());
        campanhaRecuperada.setTitulo(campanhaDTO.getTitulo());
        campanhaRecuperada.setDescricao(campanhaDTO.getDescricao());
        campanhaRecuperada.setFotoCampanha(campanhaDTO.getFotoCampanha());
        campanhaRecuperada.setStatusMeta(campanhaDTO.getStatusMeta());
        campanhaRecuperada.setSituacao(campanhaDTO.getSituacao());
        campanhaRecuperada.setUltimaAlteracao(LocalDateTime.now());
        campanhaRecuperada.setIdUsuario(usuarioCampanha.getIdUsuario());

        verificaCriadorDaCampanha(id);

        return retornarDTO(campanhaRepository.save(campanhaRecuperada));
    }

    public CampanhaDTO campanhaPeloId(Integer idCampanha) throws CampanhaNaoEncontradaException {
        CampanhaEntity campanhaEntity = buscarIdCampanha(idCampanha);
        CampanhaDTO campanhaDTO = retornarDTO(campanhaEntity);
            return campanhaDTO;
    }

    public List<CampanhaDTO> listaDeCampanhas() throws RegraDeNegocioException {
        Integer id = usuarioService.idUsuarioLogado();
        usuarioService.localizarUsuario(id);
        if (!campanhaRepository.findAll().isEmpty()){
            return campanhaRepository.findAll().stream()
                    .map(campanhaEntity -> {
                        CampanhaDTO campanhaDTO = objectMapper.convertValue(campanhaEntity, CampanhaDTO.class);
                        return campanhaDTO;
                    }).toList();
        } else {
            throw new RegraDeNegocioException("Não foi possível realizar a listagem das campanhas");
        }
    }

    public List<CampanhaDTO> listaDeCampanhasByUsuarioLogado() throws RegraDeNegocioException {
        return campanhaRepository.findAllByIdUsuario(usuarioService.idUsuarioLogado())
                .stream().map(campanhaEntity -> {
                    CampanhaDTO campanhaDTO = retornarDTO(campanhaEntity);
                    return campanhaDTO;
                }).collect(Collectors.toList());
    }

    public void deletar(Integer id) throws CampanhaNaoEncontradaException, RegraDeNegocioException {
        CampanhaEntity campanhaEntity = buscarIdCampanha(id);
        verificaCriadorDaCampanha(id);
        campanhaRepository.delete(campanhaEntity);
    }

     private CampanhaDTO retornarDTO(CampanhaEntity campanhaEntity) {
        return objectMapper.convertValue(campanhaEntity, CampanhaDTO.class);
    }

    private CampanhaEntity retornarEntity(CampanhaCreateDTO campanhaCreateDTO) {
        return objectMapper.convertValue(campanhaCreateDTO, CampanhaEntity.class);
    }

    private CampanhaEntity buscarIdCampanha(Integer id) throws CampanhaNaoEncontradaException {
        return campanhaRepository.findById(id).orElseThrow(() -> new CampanhaNaoEncontradaException("Campanha não encontrada."));
    }

    //Verifica por meio de query se pelo id da campanha há algum usuário logado
    private void verificaCriadorDaCampanha(Integer idCampanha) throws CampanhaNaoEncontradaException, RegraDeNegocioException {
        campanhaRepository.findAllByIdUsuario(usuarioService.idUsuarioLogado())
                .stream()
                .filter(campanhaEntity -> campanhaEntity.getIdCampanha().equals(idCampanha))
                .findFirst()
                .orElseThrow(() -> new CampanhaNaoEncontradaException("Campanha não encontrada"));
    }
}

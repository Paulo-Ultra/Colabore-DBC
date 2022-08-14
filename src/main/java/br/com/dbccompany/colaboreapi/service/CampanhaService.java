package br.com.dbccompany.colaboreapi.service;

import br.com.dbccompany.colaboreapi.dto.CampanhaCreateDTO;
import br.com.dbccompany.colaboreapi.dto.CampanhaDTO;
import br.com.dbccompany.colaboreapi.dto.UsuarioCreateDTO;
import br.com.dbccompany.colaboreapi.dto.UsuarioDTO;
import br.com.dbccompany.colaboreapi.entity.AutenticacaoEntity;
import br.com.dbccompany.colaboreapi.entity.CampanhaEntity;
import br.com.dbccompany.colaboreapi.entity.UsuarioEntity;
import br.com.dbccompany.colaboreapi.exceptions.RegraDeNegocioException;
import br.com.dbccompany.colaboreapi.repository.CampanhaRepository;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class CampanhaService {


    private final ObjectMapper objectMapper;

    private final AutenticacaoService autenticacaoService;

    private final UsuarioService usuarioService;

    private final CampanhaRepository campanhaRepository;

    public CampanhaDTO adicionar(CampanhaCreateDTO campanhaCreateDTO) throws RegraDeNegocioException {

        Integer id = usuarioService.idUsuarioLogado();

        UsuarioEntity usuarioRecuperado = usuarioService.localizarUsuario(id);

        CampanhaEntity campanhaEntity = retornarEntity(campanhaCreateDTO);

        campanhaEntity.setUsuario(usuarioRecuperado);
        campanhaEntity.setIdUsuario(usuarioRecuperado.getIdUsuario());

        return retornarDTO(campanhaRepository.save(campanhaEntity));
    }

    private CampanhaDTO retornarDTO(CampanhaEntity campanhaEntity) {
        return objectMapper.convertValue(campanhaEntity, CampanhaDTO.class);
    }

    private CampanhaEntity retornarEntity(CampanhaCreateDTO campanhaCreateDTO) {
        return objectMapper.convertValue(campanhaCreateDTO, CampanhaEntity.class);
    }
}

package br.com.dbccompany.colaboreapi.service;

import br.com.dbccompany.colaboreapi.dto.doador.DoadorCreateDTO;
import br.com.dbccompany.colaboreapi.dto.doador.DoadorDTO;
import br.com.dbccompany.colaboreapi.entity.DoadorEntity;
import br.com.dbccompany.colaboreapi.entity.UsuarioEntity;
import br.com.dbccompany.colaboreapi.exceptions.RegraDeNegocioException;
import br.com.dbccompany.colaboreapi.repository.DoadorRepository;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class DoadorService {

    private final DoadorRepository doadorRepository;

    private final UsuarioService usuarioService;

    private final ObjectMapper objectMapper;

    public DoadorDTO adicionar(DoadorCreateDTO doadorCreateDTO) throws RegraDeNegocioException {

        Integer idDoador = usuarioService.idUsuarioLogado();
        UsuarioEntity usuarioEntity = usuarioService.localizarUsuario(idDoador);
        DoadorEntity doadorEntity = retornarDoadorEntity(doadorCreateDTO);

        //doadorEntity.setUsuario(usuarioEntity);

        doadorRepository.save(doadorEntity);

        return retornarDoadorDTO(doadorEntity);
    }

    public DoadorEntity retornarDoadorEntity (DoadorCreateDTO doadorCreateDTO) {
        return objectMapper.convertValue(doadorCreateDTO, DoadorEntity.class);
    }

    public DoadorDTO retornarDoadorDTO (DoadorEntity doadorEntity) {
        return objectMapper.convertValue(doadorEntity, DoadorDTO.class);
    }
}

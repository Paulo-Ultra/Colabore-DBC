package br.com.dbccompany.colaboreapi.service;

import br.com.dbccompany.colaboreapi.dto.doador.DoadorCreateDTO;
import br.com.dbccompany.colaboreapi.dto.doador.DoadorDTO;
import br.com.dbccompany.colaboreapi.entity.CampanhaEntity;
import br.com.dbccompany.colaboreapi.entity.DoadorEntity;
import br.com.dbccompany.colaboreapi.entity.UsuarioEntity;
import br.com.dbccompany.colaboreapi.exceptions.RegraDeNegocioException;
import br.com.dbccompany.colaboreapi.repository.CampanhaRepository;
import br.com.dbccompany.colaboreapi.repository.DoadorRepository;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Service
@RequiredArgsConstructor
public class DoadorService {

    private final DoadorRepository doadorRepository;

    private final UsuarioService usuarioService;

    private final CampanhaService campanhaService;

    private final CampanhaRepository campanhaRepository;

    private final ObjectMapper objectMapper;

    public DoadorDTO adicionar(Integer idCampanha, DoadorCreateDTO doadorCreateDTO) throws RegraDeNegocioException {

        UsuarioEntity usuarioEntity = usuarioService.getLoggedUser();
        CampanhaEntity campanhaEntity = campanhaService.localizarCampanha(idCampanha);
        DoadorEntity doadorEntity = new DoadorEntity();

        doadorEntity.setIdUsuario(usuarioEntity.getIdUsuario());
        doadorEntity.setValor(doadorCreateDTO.getValor());

        Set<CampanhaEntity> campanhas = new HashSet<>();
        campanhas.add(campanhaEntity);

        doadorEntity.setCampanhas(campanhas);

        doadorRepository.save(doadorEntity);

        if (campanhaEntity.getEncerrarAutomaticamente().equals(true) && campanhaEntity.getStatusMeta().equals(false)) {
            if (campanhaEntity.getMeta().doubleValue() < doadorCreateDTO.getValor().doubleValue()) {
                campanhaEntity.setArrecadacao(campanhaEntity.getArrecadacao().add(doadorCreateDTO.getValor()));
                if (campanhaEntity.getArrecadacao().doubleValue() >= campanhaEntity.getMeta().doubleValue()) {
                    campanhaEntity.setStatusMeta(true);
                }
            } else {
                campanhaEntity.setArrecadacao(campanhaEntity.getArrecadacao().add(doadorCreateDTO.getValor()));
                campanhaEntity.setStatusMeta(true);
            }
            campanhaRepository.save(campanhaEntity);
        } else {
            campanhaEntity.setArrecadacao(campanhaEntity.getArrecadacao().add(doadorCreateDTO.getValor()));
            campanhaRepository.save(campanhaEntity);
        }

        return retornarDoadorDTO(doadorEntity);
    }

    public DoadorDTO retornarDoadorDTO (DoadorEntity doadorEntity) {
        return objectMapper.convertValue(doadorEntity, DoadorDTO.class);
    }
}

package br.com.dbccompany.colaboreapi.service;

import br.com.dbccompany.colaboreapi.dto.doador.DoadorCreateDTO;
import br.com.dbccompany.colaboreapi.dto.doador.DoadorDTO;
import br.com.dbccompany.colaboreapi.entity.CampanhaEntity;
import br.com.dbccompany.colaboreapi.entity.DoadorEntity;
import br.com.dbccompany.colaboreapi.entity.UsuarioEntity;
import br.com.dbccompany.colaboreapi.exceptions.DoacaoException;
import br.com.dbccompany.colaboreapi.exceptions.RegraDeNegocioException;
import br.com.dbccompany.colaboreapi.repository.CampanhaRepository;
import br.com.dbccompany.colaboreapi.repository.DoadorRepository;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.Set;

@Service
@RequiredArgsConstructor
public class DoadorService {

    private final DoadorRepository doadorRepository;

    private final UsuarioService usuarioService;

    private final CampanhaService campanhaService;

    private final CampanhaRepository campanhaRepository;

    private final ObjectMapper objectMapper;

    public DoadorDTO adicionar(Integer idCampanha, DoadorCreateDTO doadorCreateDTO) throws DoacaoException, RegraDeNegocioException {

        UsuarioEntity usuarioEntity = usuarioService.getLoggedUser();
        CampanhaEntity campanhaEntity = campanhaService.localizarCampanha(idCampanha);
        DoadorEntity doadorEntity = new DoadorEntity();

        doadorEntity.setUsuario(usuarioEntity);
        doadorEntity.setValor(doadorCreateDTO.getValor());

        Set<CampanhaEntity> campanhas = new HashSet<>();
        campanhas.add(campanhaEntity);

        doadorEntity.setCampanhas(campanhas);

        if(usuarioService.getIdLoggedUser().equals(campanhaEntity.getIdUsuario())){
            throw new DoacaoException("Você não pode doar para campanhas que criou");
        } else if(LocalDateTime.now().isAfter(campanhaEntity.getDataLimite())){
            throw new DoacaoException("Campanha encerrada, esta campanha não aceita mais doações");
        } else if (campanhaEntity.getEncerrarAutomaticamente().equals(true)) {
            if (campanhaEntity.getStatusMeta()) {
                throw new DoacaoException("Esta campanha não aceita mais doações!");
            }
        }
        compararMetaComDoacao(doadorCreateDTO, campanhaEntity);
        campanhaRepository.save(campanhaEntity);
        doadorRepository.save(doadorEntity);
        return retornarDoadorDTO(doadorEntity);
    }

    private static void compararMetaComDoacao(DoadorCreateDTO doadorCreateDTO, CampanhaEntity campanhaEntity) {
        if (campanhaEntity.getMeta().doubleValue() <= doadorCreateDTO.getValor().doubleValue()) {
            campanhaEntity.setArrecadacao(campanhaEntity.getArrecadacao().add(doadorCreateDTO.getValor()));
            campanhaEntity.setStatusMeta(true);
        } else {
            campanhaEntity.setArrecadacao(campanhaEntity.getArrecadacao().add(doadorCreateDTO.getValor()));
            if (campanhaEntity.getArrecadacao().doubleValue() >= campanhaEntity.getMeta().doubleValue()) {
                campanhaEntity.setStatusMeta(true);
            }
        }
    }

    public void verificarUsuarioComCampanha (){
        campanhaService.listaDeCampanhasByUsuarioLogado();
    }

    public DoadorDTO retornarDoadorDTO (DoadorEntity doadorEntity) {
        return objectMapper.convertValue(doadorEntity, DoadorDTO.class);
    }
}

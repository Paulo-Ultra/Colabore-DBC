package br.com.dbccompany.colaboreapi.service;

import br.com.dbccompany.colaboreapi.dto.UsuarioCreateDto;
import br.com.dbccompany.colaboreapi.dto.UsuarioDto;
import br.com.dbccompany.colaboreapi.entity.AutenticacaoEntity;
import br.com.dbccompany.colaboreapi.entity.UsuarioEntity;

import br.com.dbccompany.colaboreapi.repository.AutenticacaoRepository;
import br.com.dbccompany.colaboreapi.repository.UsuarioRepository;

import com.fasterxml.jackson.databind.ObjectMapper;

import org.springframework.beans.factory.annotation.Autowired;

import org.springframework.stereotype.Service;

@Service
public class UsuarioService {

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private UsuarioRepository usuarioRepository;

    @Autowired
    private AutenticacaoRepository autenticacaoRepository;

    @Autowired
    private AutenticacaoService autenticacaoService;

    public UsuarioDto adicionar(UsuarioCreateDto usuarioCreateDto) {

        UsuarioEntity usuarioEntity = objectMapper.convertValue(usuarioCreateDto, UsuarioEntity.class);

        UsuarioEntity usuario = usuarioRepository.save(usuarioEntity);

        AutenticacaoEntity autenticacaoEntity = objectMapper.convertValue(usuarioCreateDto.getAutenticacaoDto(), AutenticacaoEntity.class);

        autenticacaoEntity.setUsuarioEntity(usuario);

        autenticacaoEntity.setSenha(autenticacaoService.criptografarSenha(autenticacaoEntity.getSenha()));

        autenticacaoRepository.save(autenticacaoEntity);

        return objectMapper.convertValue(usuario, UsuarioDto.class);
    }
}

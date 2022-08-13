package br.com.dbccompany.colaboreapi.dto;

import lombok.Data;

@Data
public class UsuarioCreateDto {

    private String nome;

    private String foto;

    private AutenticacaoDto autenticacaoDto;
}

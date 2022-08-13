package br.com.dbccompany.colaboreapi.dto;

import lombok.Data;

@Data
public class UsuarioCreateDTO {

    private String nome;

    private String foto;

    private AutenticacaoDTO autenticacaoDto;
}

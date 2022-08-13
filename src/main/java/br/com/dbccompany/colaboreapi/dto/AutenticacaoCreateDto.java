package br.com.dbccompany.colaboreapi.dto;

import lombok.Data;

@Data
public class AutenticacaoCreateDto {

    private String email;

    private String senha;
}

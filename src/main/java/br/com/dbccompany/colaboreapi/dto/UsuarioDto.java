package br.com.dbccompany.colaboreapi.dto;

import lombok.Data;

import javax.persistence.Column;

@Data
public class UsuarioDto extends UsuarioCreateDto {

    private Integer idUsuario;
}

package br.com.dbccompany.colaboreapi.exceptions;

public class CampanhaNaoEncontradaException extends Exception {
     public CampanhaNaoEncontradaException(String mensagem){
         super(mensagem);
     }
}

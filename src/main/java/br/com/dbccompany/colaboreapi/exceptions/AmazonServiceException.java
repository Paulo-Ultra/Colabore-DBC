package br.com.dbccompany.colaboreapi.exceptions;

public class AmazonServiceException extends Exception {
    public AmazonServiceException(String mensagem){
        super(mensagem);
    }
}

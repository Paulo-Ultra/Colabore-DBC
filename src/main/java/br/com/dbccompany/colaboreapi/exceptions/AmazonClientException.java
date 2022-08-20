package br.com.dbccompany.colaboreapi.exceptions;

public class AmazonClientException extends  Exception {
    public AmazonClientException(String mensagem){
        super(mensagem);
    }
}

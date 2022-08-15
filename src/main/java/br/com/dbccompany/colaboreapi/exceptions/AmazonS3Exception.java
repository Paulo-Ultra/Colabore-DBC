package br.com.dbccompany.colaboreapi.exceptions;

public class AmazonS3Exception extends Exception {
    public AmazonS3Exception(String mensagem){
        super(mensagem);
    }
}

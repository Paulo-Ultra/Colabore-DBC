package br.com.dbccompany.colaboreapi.service;

import br.com.dbccompany.colaboreapi.exceptions.AmazonS3Exception;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.io.InputStream;
import java.net.URI;

@Service
@RequiredArgsConstructor
public class S3Service {

    @Value("${s3.bucket}")
    private String bucket;

    /*private final AmazonS3 s3Client;

    public URI uploadFile(MultipartFile multipartFile) throws AmazonS3Exception {
        try {
            InputStream inputStream = multipartFile.getInputStream();
            String fileName = multipartFile.getOriginalFilename();
            String contentType = multipartFile.getContentType();
            return uploadFile(inputStream, fileName, contentType);
        } catch (IOException ex) {
            throw new AmazonS3Exception("Erro ao salvar a imagem!");
        }
    }*/
}

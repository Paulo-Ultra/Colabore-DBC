package br.com.dbccompany.colaboreapi.service;

import br.com.dbccompany.colaboreapi.exceptions.AmazonS3Exception;
import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.model.ObjectMetadata;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.io.InputStream;
import java.net.URI;
import java.net.URISyntaxException;

@Service
@RequiredArgsConstructor
public class S3Service {

    @Value("${s3.bucket}")
    private String bucket;

    private final AmazonS3 s3Client;

    public URI uploadFile(MultipartFile multipartFile) throws AmazonS3Exception {
        try {
            InputStream inputStream = multipartFile.getInputStream();
            String fileName = multipartFile.getOriginalFilename();
            String contentType = multipartFile.getContentType();
            return uploadFile(inputStream, fileName, contentType);
        } catch (IOException ex) {
            throw new AmazonS3Exception("Erro ao salvar a imagem!");
        }
    }

    public URI uploadFile (InputStream inputStream, String fileName, String contentType) throws AmazonS3Exception {
        try{
            ObjectMetadata metadata = new ObjectMetadata();
            metadata.setContentType(contentType);
            s3Client.putObject(bucket, fileName, inputStream, metadata);
            return s3Client.getUrl(bucket, fileName).toURI();
        } catch (URISyntaxException e) {
            throw new AmazonS3Exception("Erro ao salva imagem!");
        }
    }

}

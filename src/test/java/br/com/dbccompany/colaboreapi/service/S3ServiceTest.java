package br.com.dbccompany.colaboreapi.service;

import br.com.dbccompany.colaboreapi.exceptions.AmazonS3Exception;
import com.amazonaws.services.s3.AmazonS3;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.util.ReflectionTestUtils;
import org.springframework.web.context.WebApplicationContext;

import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;

import static org.junit.Assert.assertNotNull;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.when;

@ContextConfiguration(classes = {S3Service.class})
@RunWith(MockitoJUnitRunner.class)
public class S3ServiceTest {

    @InjectMocks
    private S3Service s3Service;
    @Mock
    private AmazonS3 amazonS3;

    private MockMultipartFile mockMultipartFile;
    private URL url;

    private final String bucketName = "";

    @Before
    public void init() throws MalformedURLException {

        ReflectionTestUtils.setField(s3Service, "bucketName", "teste");

        mockMultipartFile = new MockMultipartFile(
                "foto.pdf",
                "arquivo",
                "application/jpg",
                "{key1: value1}".getBytes());

        url = new URL(
                "https",
                "stackoverflow.com",
                80, "pages/page1.html");

    }

    @Test
    public void deveTestarUploadFileComSucesso() throws AmazonS3Exception {

        doReturn(null).when(amazonS3).putObject(any(), any(), any(), any());
        when(amazonS3.getUrl(anyString(), anyString())).thenReturn(url);
        URI uri = s3Service.uploadFile(mockMultipartFile);
        assertNotNull(uri);
    }

    @Test(expected = IOException.class)
    public void deveTestarUploadFileComException() throws AmazonS3Exception, IOException {

        doReturn(null).when(amazonS3).putObject(any(), any(), any(), any());
        when(amazonS3.getUrl(anyString(), anyString())).thenReturn(url);
//        when(mockMultipartFile.getInputStream()).thenThrow(IOException.class);
        URI uri = s3Service.uploadFile(mockMultipartFile);
        assertNotNull(uri);
    }
}
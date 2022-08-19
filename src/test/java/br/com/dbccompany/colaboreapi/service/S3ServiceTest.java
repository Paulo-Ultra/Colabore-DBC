package br.com.dbccompany.colaboreapi.service;

import com.amazonaws.services.s3.AmazonS3;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.util.ReflectionTestUtils;

import static org.mockito.Mockito.when;

@ContextConfiguration(classes = {CampanhaService.class})
@RunWith(MockitoJUnitRunner.class)
public class S3ServiceTest {

    @InjectMocks
    private S3Service s3Service;

    @Mock
    private AmazonS3 amazonS3;

    private ObjectMapper objectMapper = new ObjectMapper();

    @Before
    public void init() {
        objectMapper.registerModule(new JavaTimeModule());
        objectMapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
        objectMapper.configure(SerializationFeature.FAIL_ON_EMPTY_BEANS, false);
        objectMapper.disable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS);
        ReflectionTestUtils.setField(s3Service, "objectMapper", objectMapper);
    }

    @Test
    public void deveTestarUploadFileComsucesso(){
    }

}

package edu.asu.diging.tardis.core.service.impl;

import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;

import org.junit.Before;
import org.junit.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;
import org.springframework.web.client.RestTemplate;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.context.support.AnnotationConfigContextLoader;
import org.springframework.test.util.ReflectionTestUtils;

import edu.asu.diging.gilesecosystem.requests.ICompletedStorageRequest;
import edu.asu.diging.gilesecosystem.requests.ICompletionNotificationRequest;
import edu.asu.diging.gilesecosystem.requests.IRequestFactory;
import edu.asu.diging.gilesecosystem.requests.exceptions.MessageCreationException;
import edu.asu.diging.gilesecosystem.requests.impl.CompletedStorageRequest;
import edu.asu.diging.gilesecosystem.requests.impl.CompletionNotificationRequest;
import edu.asu.diging.gilesecosystem.requests.kafka.IRequestProducer;
import edu.asu.diging.gilesecosystem.septemberutil.service.ISystemMessageHandler;
import edu.asu.diging.gilesecosystem.util.files.IFileStorageManager;
import edu.asu.diging.gilesecosystem.util.properties.IPropertiesManager;
import edu.asu.diging.tardis.config.Properties;
import edu.asu.diging.tardis.core.service.IFileService;
import edu.asu.diging.tardis.core.service.IInnogenScriptRunner;
import edu.asu.diging.tardis.core.service.IProgressManager;

@TestPropertySource("classpath*:config.properties")
public class ImageExtractionManagerTest {
    @Mock
    private IPropertiesManager propertiesManager;
    
    @Mock
    private ISystemMessageHandler messageHandler;
    
    @Mock
    private IFileStorageManager fileStorageManager;
    
    @Mock
    private IRequestFactory<ICompletionNotificationRequest, CompletionNotificationRequest> requestFactory;

    @Mock
    private IRequestProducer requestProducer;
    
    @Mock
    private IProgressManager progressManager;
    
    @Mock
    private IInnogenScriptRunner innogenScriptRunner;
    
    @Mock
    private IFileService fileService;
    
    @Mock
    private ImageProcessor processor;
    
    @Mock
    private File outputDirectoryMock;
    
    @Mock
    private RestTemplate restTemplate;
    
    @Mock
    private AExtractionManager aExtractionManager;
    
    @Mock
    protected RestTemplate restTemplateMock = Mockito.mock(RestTemplate.class);
    
    @Value("${base_directory}")
    private String base_directory;
    
    @InjectMocks
    private ImageExtractionManager imageExtractionManager = new ImageExtractionManager();
    
    private ICompletedStorageRequest iCompletedStorageRequest;
    
    @Before
    public void setUp() throws IOException {
        MockitoAnnotations.initMocks(this);
        ReflectionTestUtils.setField(imageExtractionManager, "restTemplate", restTemplateMock);
        iCompletedStorageRequest = createICompletedStorageRequest(1);
        Mockito.when(processor.saveImageFile(Mockito.any(BufferedImage.class), Mockito.any(ICompletedStorageRequest.class))).thenReturn("Users/dabiju/giles-eco-tardis/data/files/github_37469232/UPPzI36a0QHiRF/DOCYe3yl6zWuYFX/HW3-DiyaBiju.pdf.1.tiff");
        Mockito.when(outputDirectoryMock.listFiles()).thenReturn(new File[]{new File("testFile.txt")});
        Mockito.when(propertiesManager.getProperty(Properties.KAFKA_TOPIC_COMPLETION_NOTIFICATIION)).thenReturn("topic_completion_notification");
        byte[] mockImage = new byte[10];
        ResponseEntity<byte[]> mockResponse = new ResponseEntity<>(mockImage, HttpStatus.OK);
        Mockito.when(restTemplateMock.exchange(Mockito.anyString(), Mockito.eq(HttpMethod.GET), Mockito.any(HttpEntity.class), Mockito.eq(byte[].class)))
                .thenReturn(mockResponse);
        Mockito.when(propertiesManager.getProperty(Properties.APP_URL)).thenReturn("http://localhost:8088/tardis");
        createDirectoryAndFile("/files/github_37469232/UPPzI36a0QHiRF/DOCYe3yl6zWuYFX/extracted/1/extracted", "HW3-DiyaBiju.pdf.1.tiff");
        System.out.println(propertiesManager.getProperty(Properties.BASE_DIRECTORY));
    }
    
    
    @Test
    public void test_extractImages_whenGetImageExtractedIsFalse_success() throws MessageCreationException {
        imageExtractionManager.extractImages(iCompletedStorageRequest);
        Mockito.verify(requestProducer, Mockito.times(1)).sendRequest(Mockito.any(ICompletionNotificationRequest.class), propertiesManager.getProperty(Properties.KAFKA_TOPIC_COMPLETION_NOTIFICATIION));
    }
    
    private ICompletedStorageRequest createICompletedStorageRequest(int pageNr) {
        ICompletedStorageRequest iCompletedStorageRequest = new CompletedStorageRequest();
        iCompletedStorageRequest.setDocumentId("DOCYe3yl6zWuYFX");
        iCompletedStorageRequest.setDownloadUrl("http://localhost:8082/nepomuk/rest/files/FILEl17JlMz3axqi");
        iCompletedStorageRequest.setDownloadPath("http://localhost:8082/nepomuk/rest/files/FILEl17JlMz3axqi");
        iCompletedStorageRequest.setUsername("github_37469232");
        iCompletedStorageRequest.setFilename("HW3-DiyaBiju.pdf.1.tiff");
        iCompletedStorageRequest.setUploadId("UPPzI36a0QHiRF");
        iCompletedStorageRequest.setPageNr(pageNr);
        return iCompletedStorageRequest;
    }
    
    private void createDirectoryAndFile(String path, String fileName) throws IOException {
        System.out.println(base_directory+path);
        File folder = new File(base_directory + path);
        folder.mkdirs();
        File file = new File(folder, fileName);
        file.createNewFile();
    }
}

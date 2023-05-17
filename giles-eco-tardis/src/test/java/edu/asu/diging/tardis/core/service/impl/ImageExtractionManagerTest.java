package edu.asu.diging.tardis.core.service.impl;

import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.util.Arrays;

import javax.imageio.ImageIO;

import org.junit.Before;
import org.junit.Test;
import org.mockito.ArgumentMatchers;
import org.mockito.InjectMocks;
import org.mockito.Matchers;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;
import org.springframework.web.client.RestTemplate;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;

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
    private RestTemplate restTemplateMock;
    
    @InjectMocks
    private ImageExtractionManager imageExtractionManager = new ImageExtractionManager();
    
    private ICompletedStorageRequest iCompletedStorageRequest;
    
    @Before
    public void setUp() throws IOException {
        MockitoAnnotations.initMocks(this);
        iCompletedStorageRequest = createICompletedStorageRequest(1);
        Mockito.when(processor.saveImageFile(Mockito.any(BufferedImage.class), Mockito.any(ICompletedStorageRequest.class))).thenReturn("Users/dabiju/giles-eco-tardis/data/files/github_37469232/UPPzI36a0QHiRF/DOCYe3yl6zWuYFX/HW3-DiyaBiju.pdf.1.tiff");
        File file = new File("path/to/file.txt");
        Mockito.when(outputDirectoryMock.listFiles()).thenReturn(new File[]{file});
        Mockito.when(propertiesManager.getProperty(Properties.KAFKA_TOPIC_COMPLETION_NOTIFICATIION)).thenReturn("topic_completion_notification");
        ResponseEntity<byte[]> responseMock = new ResponseEntity<>(new byte[0], HttpStatus.OK);
        Mockito.when(restTemplateMock.exchange(Mockito.anyString(),
                Mockito.<HttpMethod>any(),
                Mockito.<HttpEntity<?>>any(),
                Mockito.<Class<byte[]>>any())
                ).thenReturn(responseMock);
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
        
        return iCompletedStorageRequest;
    } 
}

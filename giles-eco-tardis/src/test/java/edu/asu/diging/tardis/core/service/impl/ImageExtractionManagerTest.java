package edu.asu.diging.tardis.core.service.impl;

import java.io.File;
import java.io.IOException;

import org.junit.Before;
import org.junit.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;
import org.springframework.web.client.RestTemplate;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
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
import edu.asu.diging.tardis.core.exception.InnogenScriptRunnerException;
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
    private File outputDirectoryMock;
    
    @Mock
    private RestTemplate restTemplate;
    
    @Mock
    private AExtractionManager aExtractionManager;
    
    @Mock
    protected RestTemplate restTemplateMock = Mockito.mock(RestTemplate.class);
    
    @InjectMocks
    private ImageExtractionManager imageExtractionManager = new ImageExtractionManager();
    
    private ICompletedStorageRequest iCompletedStorageRequest;
    
    private ICompletionNotificationRequest completedRequest;
    
    @Before
    public void setUp() throws IOException, InstantiationException, IllegalAccessException {
        MockitoAnnotations.initMocks(this);
        ReflectionTestUtils.setField(imageExtractionManager, "restTemplate", restTemplateMock);
        iCompletedStorageRequest = createICompletedStorageRequest(1);
        Mockito.when(propertiesManager.getProperty(Properties.KAFKA_TOPIC_COMPLETION_NOTIFICATIION)).thenReturn("topic_completion_notification");
        Mockito.when(propertiesManager.getProperty(Properties.EXTRACTED_FOLDER)).thenReturn("extracted");
        byte[] mockImage = new byte[10];
        ResponseEntity<byte[]> mockResponse = new ResponseEntity<>(mockImage, HttpStatus.OK);
        Mockito.when(restTemplateMock.exchange(Mockito.anyString(), Mockito.eq(HttpMethod.GET), Mockito.any(HttpEntity.class), Mockito.eq(byte[].class)))
                .thenReturn(mockResponse);
        Mockito.when(propertiesManager.getProperty(Properties.APP_URL)).thenReturn("http://localhost:8088/tardis");
        createDirectoryAndFile("files/github_37469232/UPPzI36a0QHiRF/DOCYe3yl6zWuYFX/extracted/1/extracted", "HW3-DiyaBiju.jpg");
        completedRequest = new CompletionNotificationRequest();
        completedRequest.setRequestId(iCompletedStorageRequest.getRequestId());
        completedRequest.setUploadId(iCompletedStorageRequest.getUploadId());
        Mockito.when(requestFactory.createRequest(iCompletedStorageRequest.getRequestId(), iCompletedStorageRequest.getUploadId())).thenReturn(completedRequest);
        Mockito.when(fileService.saveImageFile(Mockito.any(), Mockito.any())).thenReturn("files/github_37469232/UPPzI36a0QHiRF/DOCYe3yl6zWuYFX/HW3-DiyaBiju.pdf.1.tiff");
    }
    
    
    @Test
    public void test_extractImages_whenIsImageExtractedIsFalse_success() throws MessageCreationException {
        imageExtractionManager.extractImages(iCompletedStorageRequest);
        Mockito.verify(requestProducer, Mockito.times(1)).sendRequest(completedRequest, propertiesManager.getProperty(Properties.KAFKA_TOPIC_COMPLETION_NOTIFICATIION));
        cleanUpFiles();
    }
    
    @Test
    public void test_extractImages_whenIsImageExtractedIsFalse_throwsIOException() throws MessageCreationException, IOException {
        Mockito.when(fileService.saveImageFile(Mockito.any(), Mockito.any())).thenThrow(IOException.class);
        imageExtractionManager.extractImages(iCompletedStorageRequest);
        Mockito.verify(requestProducer, Mockito.times(1)).sendRequest(completedRequest, propertiesManager.getProperty(Properties.KAFKA_TOPIC_COMPLETION_NOTIFICATIION));
        Mockito.verify(messageHandler, Mockito.times(1)).handleMessage(Mockito.anyString(), Mockito.any(IOException.class), Mockito.any());
        cleanUpFiles();
    }
    
    @Test
    public void test_extractImages_whenIsImageExtractedIsFalse_throwsInnogenScriptRunnerException() throws MessageCreationException, InnogenScriptRunnerException {
        Mockito.when(innogenScriptRunner.runInnogenScript(Mockito.anyString())).thenThrow(InnogenScriptRunnerException.class);
        imageExtractionManager.extractImages(iCompletedStorageRequest);
        Mockito.verify(requestProducer, Mockito.times(1)).sendRequest(completedRequest, propertiesManager.getProperty(Properties.KAFKA_TOPIC_COMPLETION_NOTIFICATIION));
        Mockito.verify(messageHandler, Mockito.times(1)).handleMessage(Mockito.anyString(), Mockito.any(IOException.class), Mockito.any());
        cleanUpFiles();
    }
    
    @Test
    public void test_extractImages_whenIsImageExtractedIsTrue_success() throws MessageCreationException {
        imageExtractionManager.extractImages(iCompletedStorageRequest);
        Mockito.verify(requestProducer, Mockito.times(0)).sendRequest(completedRequest, propertiesManager.getProperty(Properties.KAFKA_TOPIC_COMPLETION_NOTIFICATIION));
        cleanUpFiles();
    }
    
    @Test
    public void test_extractImages_whenThereAreNoExtractedImages_success() throws MessageCreationException {
        cleanUpFiles();
        imageExtractionManager.extractImages(iCompletedStorageRequest);
        Mockito.verify(requestProducer, Mockito.times(1)).sendRequest(completedRequest, propertiesManager.getProperty(Properties.KAFKA_TOPIC_COMPLETION_NOTIFICATIION));
    }
    
    private ICompletedStorageRequest createICompletedStorageRequest(int pageNr) {
        ICompletedStorageRequest iCompletedStorageRequest = new CompletedStorageRequest();
        iCompletedStorageRequest.setDocumentId("DOCYe3yl6zWuYFX");
        iCompletedStorageRequest.setDownloadUrl("http://localhost:8082/nepomuk/rest/files/FILEl17JlMz3axqi");
        iCompletedStorageRequest.setDownloadPath("http://localhost:8082/nepomuk/rest/files/FILEl17JlMz3axqi");
        iCompletedStorageRequest.setUsername("github_37469232");
        iCompletedStorageRequest.setFilename("HW3-DiyaBiju.pdf.1.tiff");
        iCompletedStorageRequest.setUploadId("UPPzI36a0QHiRF");
        iCompletedStorageRequest.setRequestId("REQ12345");
        return iCompletedStorageRequest;
    }
    
    private void createDirectoryAndFile(String path, String fileName) throws IOException {
        File folder = new File(path);
        folder.mkdirs();
        File file = new File(folder, fileName);
        file.createNewFile();
    }
    
    private void cleanUpFiles() {
        File directory = new File("files");
        deleteDirectory(directory);
    }
    
    public void deleteDirectory(File directory) {
        if (directory.exists()) {
            File[] files = directory.listFiles();
            if (files != null) {
                for (File file : files) {
                    if (file.isDirectory()) {
                        deleteDirectory(file);
                    } else {
                        file.delete();
                    }
                }
            }
            directory.delete();
        }
    }
}

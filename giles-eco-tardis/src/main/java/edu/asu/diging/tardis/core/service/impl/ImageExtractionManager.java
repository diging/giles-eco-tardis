package edu.asu.diging.tardis.core.service.impl;

import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.OffsetDateTime;
import java.time.ZoneId;

import javax.annotation.PostConstruct;
import javax.imageio.ImageIO;
import javax.inject.Inject;
import javax.inject.Named;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import edu.asu.diging.gilesecosystem.requests.ICompletedStorageRequest;
import edu.asu.diging.gilesecosystem.requests.ICompletionNotificationRequest;
import edu.asu.diging.gilesecosystem.requests.IRequestFactory;
import edu.asu.diging.gilesecosystem.requests.RequestStatus;
import edu.asu.diging.gilesecosystem.requests.exceptions.MessageCreationException;
import edu.asu.diging.gilesecosystem.requests.impl.CompletionNotificationRequest;
import edu.asu.diging.gilesecosystem.requests.kafka.IRequestProducer;
import edu.asu.diging.gilesecosystem.septemberutil.properties.MessageType;
import edu.asu.diging.gilesecosystem.septemberutil.service.ISystemMessageHandler;
import edu.asu.diging.gilesecosystem.util.properties.IPropertiesManager;
import edu.asu.diging.tardis.api.v1.DownloadFileController;
import edu.asu.diging.tardis.config.Properties;
import edu.asu.diging.tardis.core.exception.InnogenScriptRunnerException;
import edu.asu.diging.tardis.core.service.IFileService;
import edu.asu.diging.tardis.core.service.IImageExtractionManager;
import edu.asu.diging.tardis.core.service.IInnogenScriptRunner;
import edu.asu.diging.tardis.core.service.IProgressManager;
import edu.asu.diging.tardis.core.service.ProgressPhase;

@Service
public class ImageExtractionManager extends AExtractionManager implements IImageExtractionManager {

    private final Logger logger = LoggerFactory.getLogger(getClass());

    @Autowired
    private IPropertiesManager propertiesManager;

    @Autowired
    private ISystemMessageHandler messageHandler;

    @Autowired
    private IRequestFactory<ICompletionNotificationRequest, CompletionNotificationRequest> requestFactory;

    @Autowired
    private IRequestProducer requestProducer;
    
    @Autowired
    private IProgressManager progressManager;
    
    @Autowired
    private IInnogenScriptRunner innogenScriptRunner;
    
    @Autowired
    private IFileService fileService;
    
    @Inject
    @Named("restTemplate")
    private RestTemplate restTemplate;
    
    @PostConstruct
    public void init() {
        /*
         * Recommended fix for performance issues due to colors: "Due to the change of
         * the java color management module towards “LittleCMS”, users can experience
         * slow performance in color operations. Solution: disable LittleCMS in favour
         * of the old KCMS (Kodak Color Management System)"
         */
        //System.setProperty("sun.java2d.cmm", "sun.java2d.cmm.kcms.KcmsServiceProvider");

        requestFactory.config(CompletionNotificationRequest.class);
    }

    @Override
    public void extractImages(ICompletedStorageRequest request) {
        // if the image is already extracted by services like imogen or tardis itself we can skip porcessing.
        if (request.isDerivedFile()) {
            return;
        }
        
        logger.info("Extracting images for: " + request.getDownloadPath());
        
        BufferedImage imageFile = null;
        RequestStatus status = RequestStatus.COMPLETE;
        try {
            imageFile = ImageIO.read((new ByteArrayInputStream(downloadFile(request.getDownloadUrl(), restTemplate))));  
        } catch (IOException e) {
            messageHandler.handleMessage("Could get Image for " + request.getDownloadPath(), e, MessageType.ERROR);
            sendCompletionRequest(request, RequestStatus.FAILED);
            return;
        }
        String imagePath;
        try {
           imagePath = fileService.saveImageFile(imageFile, request);
        } catch (IOException e) {
            messageHandler.handleMessage("Could not save image file " + request.getDownloadPath(), e, MessageType.ERROR);
            sendCompletionRequest(request, RequestStatus.FAILED);
            return;
        }
        String uniqueFolder;
        try {
            uniqueFolder = innogenScriptRunner.runInnogenScript(imagePath);
        } catch (InnogenScriptRunnerException e) {
            messageHandler.handleMessage("Could not run docker command" , e, MessageType.ERROR);
            sendCompletionRequest(request, RequestStatus.FAILED);
            return;
        }
        Path path = Paths.get(imagePath);
        String outputParentFolderPath = path.getParent().toString() + File.separator + propertiesManager.getProperty(Properties.EXTRACTED_FOLDER) + File.separator + uniqueFolder + File.separator + propertiesManager.getProperty(Properties.EXTRACTED_FOLDER);
        File outputDirectory = new File(outputParentFolderPath);
        File[] files = outputDirectory.listFiles();
        if (files == null || files.length == 0) {
            fileService.deleteFile(request.getUsername(), request.getUploadId(), request.getDocumentId(), uniqueFolder, null);
            sendCompletionRequest(request, RequestStatus.COMPLETE);
            return;
        }
        for (File file : files) {
            sendCompletionRequest(request, status, file, uniqueFolder);
            fileService.deleteFile(request.getUsername(), request.getUploadId(), request.getDocumentId(), uniqueFolder, file.getName());
        }
        progressManager.setPhase(ProgressPhase.WIND_DOWN);
        progressManager.reset();  
    }
    
    private void sendCompletionRequest(ICompletedStorageRequest request, RequestStatus status) {
        ICompletionNotificationRequest completedRequest = null;
        try {
          completedRequest = requestFactory.createRequest(request.getRequestId(), request.getUploadId());
        } catch (InstantiationException | IllegalAccessException e) {
            messageHandler.handleMessage("Could not create request.", e, MessageType.ERROR);
        }
        completedRequest.setDocumentId(request.getDocumentId());
        completedRequest.setFileId(request.getFileId());
        completedRequest.setNotifier(propertiesManager.getProperty(Properties.NOTIFIER_ID));
        completedRequest.setStatus(status);
        completedRequest.setExtractionDate(OffsetDateTime.now(ZoneId.of("UTC")).toString());
        completedRequest.setDerivedFile(true);
        completedRequest.setContentType("image/png");
        progressManager.setPhase(ProgressPhase.DONE);
        try {
            requestProducer.sendRequest(completedRequest,
            propertiesManager.getProperty(Properties.KAFKA_TOPIC_COMPLETION_NOTIFICATIION));
        } catch (MessageCreationException e) {
            messageHandler.handleMessage("Could not send message.", e, MessageType.ERROR);
        }
    }
    
    private void sendCompletionRequest(ICompletedStorageRequest request, RequestStatus status, File file, String uniqueFolder) {
        ICompletionNotificationRequest completedRequest = null;
        try {
          completedRequest = requestFactory.createRequest(request.getRequestId(), request.getUploadId());
        } catch (InstantiationException | IllegalAccessException e) {
            messageHandler.handleMessage("Could not create request.", e, MessageType.ERROR);
        }
        String restEndpoint = getRestEndpoint();
        completedRequest.setDocumentId(request.getDocumentId());
        completedRequest.setFileId(request.getFileId());
        completedRequest.setNotifier(propertiesManager.getProperty(Properties.NOTIFIER_ID));
        completedRequest.setStatus(status);
        completedRequest.setExtractionDate(OffsetDateTime.now(ZoneId.of("UTC")).toString());
        completedRequest.setFilename(file.getName());
        completedRequest.setDerivedFile(true);
        completedRequest.setContentType("image/png");
        completedRequest.setDownloadUrl(restEndpoint + DownloadFileController.GET_FILE_URL
                .replace(DownloadFileController.USER_NAME_PLACEHOLDER, request.getUsername())
                .replace(DownloadFileController.UPLOAD_ID_PLACEHOLDER, request.getUploadId())
                .replace(DownloadFileController.DOCUMENT_ID_PLACEHOLDER, request.getDocumentId())
                .replace(DownloadFileController.UNIQUE_FOLDER, uniqueFolder)
                .replace(DownloadFileController.FILENAME_PLACEHOLDER, file.getName()));
        progressManager.setPhase(ProgressPhase.DONE);
        try {
            requestProducer.sendRequest(completedRequest,
            propertiesManager.getProperty(Properties.KAFKA_TOPIC_COMPLETION_NOTIFICATIION));
        } catch (MessageCreationException e) {
            messageHandler.handleMessage("Could not send message.", e, MessageType.ERROR);
        }
    }
}

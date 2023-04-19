package edu.asu.diging.tardis.core.service.impl;

import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.OffsetDateTime;
import java.time.ZoneId;
import java.util.ArrayList;
import java.util.List;

import javax.annotation.PostConstruct;
import javax.imageio.ImageIO;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import edu.asu.diging.gilesecosystem.requests.ICompletedStorageRequest;
import edu.asu.diging.gilesecosystem.requests.ICompletionNotificationRequest;
import edu.asu.diging.gilesecosystem.requests.IRequestFactory;
import edu.asu.diging.gilesecosystem.requests.PageStatus;
import edu.asu.diging.gilesecosystem.requests.RequestStatus;
import edu.asu.diging.gilesecosystem.requests.exceptions.MessageCreationException;
import edu.asu.diging.gilesecosystem.requests.impl.CompletionNotificationRequest;
import edu.asu.diging.gilesecosystem.requests.impl.PageElement;
import edu.asu.diging.gilesecosystem.requests.kafka.IRequestProducer;
import edu.asu.diging.gilesecosystem.septemberutil.properties.MessageType;
import edu.asu.diging.gilesecosystem.septemberutil.service.ISystemMessageHandler;
import edu.asu.diging.gilesecosystem.util.files.IFileStorageManager;
import edu.asu.diging.gilesecosystem.util.properties.IPropertiesManager;
import edu.asu.diging.tardis.api.DownloadFileController;
import edu.asu.diging.tardis.config.Properties;
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
    private IFileStorageManager fileStorageManager;

    @Autowired
    private IRequestFactory<ICompletionNotificationRequest, CompletionNotificationRequest> requestFactory;

    @Autowired
    private IRequestProducer requestProducer;
    
    @Autowired
    private IProgressManager progressManager;
    
    @Autowired
    private IInnogenScriptRunner innogenScriptRunner;
    
    
    
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

    /*
     * (non-Javadoc)
     * 
     * @see edu.asu.diging.gilesecosystem.cepheus.service.pdf.impl.
     * IImageExtractionManager #extractImages(edu.asu.diging.gilesecosystem.requests
     * .IImageExtractionRequest)
     */
    @Override
    public void extractImages(ICompletedStorageRequest request) {
        logger.info("Extracting images for: " + request.getDownloadPath());
        
        BufferedImage imageFile = null;
        RequestStatus status = RequestStatus.COMPLETE;
        try {
            imageFile = ImageIO.read((new ByteArrayInputStream(downloadFile(request.getDownloadUrl()))));
            
        } catch (IOException e) {
            messageHandler.handleMessage("Could get Image for " + request.getDownloadPath(), e, MessageType.ERROR);
            status = RequestStatus.FAILED;
        }
        ImageProcessor processor = new ImageProcessor(fileStorageManager, request);
        String imagePath, outputParentFolderPath;
        try {
            imagePath = processor.saveImageFile(imageFile, request);
            innogenScriptRunner.runInnogenScript(imagePath, request.getUsername(), request.getDocumentId(), request.getUploadId());
            Path path = Paths.get(imagePath);
            outputParentFolderPath = path.getParent().toString() + "/extracted/extracted";
            File outputDirectory = new File(outputParentFolderPath);
            File[] files = outputDirectory.listFiles();
            String restEndpoint = getRestEndpoint();
            List<edu.asu.diging.gilesecosystem.requests.impl.Page> pages = new ArrayList<>();
            edu.asu.diging.gilesecosystem.requests.impl.Page requestPage = new edu.asu.diging.gilesecosystem.requests.impl.Page();
            requestPage.setPageElements(new ArrayList<>());
            requestPage.setPageNr(request.getPageNr());
            for (File file : files) {
                PageElement pageElem = new PageElement();
                pageElem.setContentType("image/png");
                pageElem.setFilename(file.getName());
                pageElem.setType("IMAGE");
                pageElem.setDownloadUrl(
                      restEndpoint + DownloadFileController.GET_FILE_URL
                              .replace(
                                      DownloadFileController.REQUEST_ID_PLACEHOLDER,
                                      request.getRequestId())
                              .replace(
                                      DownloadFileController.DOCUMENT_ID_PLACEHOLDER,
                                      request.getDocumentId())
                              .replace(DownloadFileController.FILENAME_PLACEHOLDER,
                                      file.getName()));
                pageElem.setStatus(PageStatus.COMPLETE);
                requestPage.getPageElements().add(pageElem);
            }
            pages.add(requestPage);
            progressManager.setPhase(ProgressPhase.WIND_DOWN);
            ICompletionNotificationRequest completedRequest = null;
            try {
              completedRequest = requestFactory.createRequest(request.getRequestId(), request.getUploadId());
            } catch (InstantiationException | IllegalAccessException e) {
              messageHandler.handleMessage("Could not create request.", e, MessageType.ERROR);
              // this should never happen if used correctly
            }

            completedRequest.setDocumentId(request.getDocumentId());
            completedRequest.setFileId(request.getFileId());
            completedRequest.setNotifier(propertiesManager.getProperty(Properties.NOTIFIER_ID));
            completedRequest.setStatus(status);
            completedRequest.setExtractionDate(OffsetDateTime.now(ZoneId.of("UTC")).toString());
            completedRequest.setPages(pages);
            progressManager.setPhase(ProgressPhase.DONE);
            try {
              requestProducer.sendRequest(completedRequest,
                      propertiesManager.getProperty(Properties.KAFKA_TOPIC_COMPLETION_NOTIFICATIION));
            } catch (MessageCreationException e) {
              messageHandler.handleMessage("Could not send message.", e, MessageType.ERROR);
            }
          
            progressManager.reset();
        } catch (IOException e) {
            messageHandler.handleMessage("Could execute docker command for " + request.getDownloadPath(), e, MessageType.ERROR);
        }
    }
}

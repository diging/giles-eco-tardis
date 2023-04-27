package edu.asu.diging.tardis.core.kafka;

import java.io.IOException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.PropertySource;
import org.springframework.kafka.annotation.KafkaListener;

import com.fasterxml.jackson.databind.ObjectMapper;

import edu.asu.diging.gilesecosystem.requests.FileType;
import edu.asu.diging.gilesecosystem.requests.ICompletedStorageRequest;
import edu.asu.diging.gilesecosystem.requests.impl.CompletedStorageRequest;
import edu.asu.diging.gilesecosystem.septemberutil.properties.MessageType;
import edu.asu.diging.gilesecosystem.septemberutil.service.ISystemMessageHandler;
import edu.asu.diging.tardis.core.service.IImageExtractionManager;

@PropertySource("classpath:/config.properties")
public class RequestReceiver {

    private final Logger logger = LoggerFactory.getLogger(getClass());
    
    @Autowired
    private IImageExtractionManager manager;    
    
    @Autowired
    private ISystemMessageHandler messageHandler;
    
    @KafkaListener(id="tardis.extraction", topics = "${topic_storage_request_complete}")
    public void receiveMessage(String message) {
        ObjectMapper mapper = new ObjectMapper();
        ICompletedStorageRequest request = null;
        try {
            request = mapper.readValue(message, CompletedStorageRequest.class);
        } catch (IOException e) {
            messageHandler.handleMessage("Could not unmarshall request.", e, MessageType.ERROR);
            // FIXME: handle this case
            return;
        }
        
        if (request.getFileType() == FileType.IMAGE) {
            manager.extractImages(request);
        } else {
            // otherwise do nothing (we only care about pdfs)
            logger.info("File not an image. Skip processing.");
        } 
    }
}

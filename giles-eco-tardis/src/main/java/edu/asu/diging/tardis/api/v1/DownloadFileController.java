package edu.asu.diging.tardis.api.v1;

import java.io.IOException;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.tika.Tika;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import edu.asu.diging.gilesecosystem.septemberutil.properties.MessageType;
import edu.asu.diging.gilesecosystem.septemberutil.service.ISystemMessageHandler;
import edu.asu.diging.tardis.core.service.IFileService;

@RestController
public class DownloadFileController {
    private final Logger logger = LoggerFactory.getLogger(getClass());
    
    public final static String FILENAME_PLACEHOLDER = "{filename:.+}";
    public final static String DOCUMENT_ID_PLACEHOLDER = "{documentId}";
    public final static String USER_NAME_PLACEHOLDER = "{userName}";
    public final static String UPLOAD_ID_PLACEHOLDER = "{uploadId}";
    public final static String PAGE_NR = "{pageNr}";
    public final static String GET_FILE_URL = "/api/v1/image/" + USER_NAME_PLACEHOLDER + "/" + UPLOAD_ID_PLACEHOLDER + "/" + DOCUMENT_ID_PLACEHOLDER + "/" + PAGE_NR + "/" + FILENAME_PLACEHOLDER;
   
    @Autowired
    private IFileService fileService;
    
    @Autowired
    private ISystemMessageHandler messageHandler;

    @RequestMapping(value = GET_FILE_URL)
    public ResponseEntity<String> getFile(
            @PathVariable String filename, @PathVariable String documentId, @PathVariable String uploadId, @PathVariable String userName, @PathVariable int pageNr,
            HttpServletResponse response,
            HttpServletRequest request) {

        byte[] content;
        try {
            content = fileService.getFileContent(userName, uploadId, documentId, pageNr, filename);
        } catch (IOException e) {
            messageHandler.handleMessage("Could not read the extracted file.", e, MessageType.ERROR);
            return new ResponseEntity<String>(HttpStatus.INTERNAL_SERVER_ERROR);
        }
        
        if (content == null) {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
        
        fileService.deleteFile(userName, uploadId, documentId, pageNr, filename);
        
        String contentType = new Tika().detect(content);
        response.setContentType(contentType);
        
        response.setContentLength(content.length);
        response.setHeader("Content-disposition", "filename=\"" + filename + "\""); 
        try {
            response.getOutputStream().write(content);
            response.getOutputStream().close();
        } catch (IOException e) {
            logger.error("Could not write to output stream.", e);
            return new ResponseEntity<String>(HttpStatus.INTERNAL_SERVER_ERROR);
        }

        return new ResponseEntity<String>(HttpStatus.OK);
    }
}

package edu.asu.diging.tardis.core.service.impl;

import java.io.File;
import java.io.IOException;
import java.nio.file.Paths;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.nio.file.Files;
import java.nio.file.Path;

import edu.asu.diging.gilesecosystem.septemberutil.properties.MessageType;
import edu.asu.diging.gilesecosystem.septemberutil.service.ISystemMessageHandler;
import edu.asu.diging.gilesecosystem.util.properties.IPropertiesManager;
import edu.asu.diging.tardis.config.Properties;
import edu.asu.diging.tardis.core.service.IInnogenScriptRunner;

@Service
public class InnogenScriptRunner implements IInnogenScriptRunner{
    
    @Autowired
    private ISystemMessageHandler messageHandler;
    
    @Autowired
    protected IPropertiesManager propertiesManager;
    
    @Override
    public void runInnogenScript(String imagePath, String userName, String documentId, String uploadId) {
        String outputDirectory = createOutputDirectoryForImage(userName, documentId, uploadId);
        String dockerCommand = "docker run --mount type=bind,source=\"$(pwd)\",target=" + propertiesManager.getProperty(Properties.BASE_DIRECTORY) + 
                "extract_imgs -f " + imagePath + " -o " + outputDirectory;
        try {
            Process process = Runtime.getRuntime().exec(dockerCommand);
            process.waitFor();
        } catch (IOException | InterruptedException e) {
            messageHandler.handleMessage("Could not execute docker command for " + imagePath, e, MessageType.ERROR);
        }
    }
    
    private String createOutputDirectoryForImage(String userName, String documentId, String uploadId) {
        String path = propertiesManager.getProperty(Properties.BASE_DIRECTORY) + File.separator + "extracted" + File.separator + 
                userName + File.separator + uploadId + File.separator + documentId;
        File dirFile = new File(path);
        if (!dirFile.exists()) {
            dirFile.mkdirs();
        }
        return path;
    }

}

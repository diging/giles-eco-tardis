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
        Path path = Paths.get(imagePath);
        String outputParentFolderPath = path.getParent().toString();
        String outputDirectory = getOutputDirectoryForImage(outputParentFolderPath);
        
        String dockerCommand = propertiesManager.getProperty(Properties.DOCKER_PATH) + " run --mount type=bind,source=" + propertiesManager.getProperty(Properties.BASE_DIRECTORY)+",target=/data extract_imgs -f " + imagePath.substring(imagePath.indexOf("/data")).toString() + " -o " + outputDirectory;
        System.out.println(imagePath);
        System.out.println(outputDirectory);
        System.out.println(dockerCommand);
        try {
            Process process = Runtime.getRuntime().exec(dockerCommand);
            process.waitFor();
        } catch (IOException | InterruptedException e) {
            messageHandler.handleMessage("Could not execute docker command for " + imagePath, e, MessageType.ERROR);
        }
    }
    
    private String getOutputDirectoryForImage(String outputParentFolderPath) {
        String path =  outputParentFolderPath + "/extracted";
        File dirFile = new File(path);
        if (!dirFile.exists()) {
            dirFile.mkdirs();
        }
        return path.substring(path.indexOf("/data"));
    }

}

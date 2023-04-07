package edu.asu.diging.tardis.core.service.impl;

import java.io.IOException;
import java.nio.file.Paths;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

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
    public void runInnogenScript(String imagePath) throws IOException, InterruptedException {
        String dockerCommand = "docker run --mount type=bind,source=\"$(pwd)\",target=" + propertiesManager.getProperty(Properties.BASE_DIRECTORY) + "extract_imgs -f " + propertiesManager.getProperty(Properties.BASE_DIRECTORY) + "/" + imagePath + " -o " + propertiesManager.getProperty(Properties.BASE_DIRECTORY) + "/extracted";
        Process process = Runtime.getRuntime().exec(dockerCommand);

        int exitCode = process.waitFor();
        
        if (exitCode != 0) {
            messageHandler.handleMessage("Could execute docker command for " + imagePath, "Docker command failed during Image Extraction for " + imagePath, MessageType.ERROR);
        }
    }

}

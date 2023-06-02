package edu.asu.diging.tardis.core.service.impl;

import java.io.File;
import java.io.IOException;
import java.nio.file.Paths;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.nio.file.Path;

import edu.asu.diging.gilesecosystem.septemberutil.properties.MessageType;
import edu.asu.diging.gilesecosystem.septemberutil.service.ISystemMessageHandler;
import edu.asu.diging.gilesecosystem.util.properties.IPropertiesManager;
import edu.asu.diging.tardis.config.Properties;
import edu.asu.diging.tardis.core.exception.InnogenScriptRunnerException;
import edu.asu.diging.tardis.core.service.IInnogenScriptRunner;

@Service
public class InnogenScriptRunner implements IInnogenScriptRunner{
    
    @Autowired
    protected ISystemMessageHandler messageHandler;
    
    @Autowired
    protected IPropertiesManager propertiesManager;
    
    @Override
    public void runInnogenScript(String imagePath, int pareNr) throws InnogenScriptRunnerException {
        Path path = Paths.get(imagePath);
        String outputParentFolderPath = path.getParent().toString();
        String outputDirectory = getOutputDirectoryForImage(outputParentFolderPath, pareNr);
        String[] dockerCommand = new String[]{
                propertiesManager.getProperty(Properties.DOCKER_PATH),
                "run",
                "--mount",
                "type=bind,source=" + propertiesManager.getProperty(Properties.BASE_DIRECTORY) + ",target=/data",
                "extract_imgs",
                "-f",
                imagePath.substring(imagePath.indexOf("/data")).toString(),
                "-o",
                outputDirectory
        };
        try {
            Process process = Runtime.getRuntime().exec(dockerCommand);
            process.waitFor();
        } catch (IOException | InterruptedException e) {
            messageHandler.handleMessage("Could not execute docker command for " + imagePath, e, MessageType.ERROR);
            throw new InnogenScriptRunnerException("Could not execute docker command for " + imagePath);
        }
    }
    
    private String getOutputDirectoryForImage(String outputParentFolderPath, int pageNr) {
        String path =  outputParentFolderPath + "/extracted" + File.separator + pageNr;
        File dirFile = new File(path);
        if (!dirFile.exists()) {
            dirFile.mkdirs();
        }
        return path.substring(path.indexOf("/data"));
    }

}

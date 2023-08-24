package edu.asu.diging.tardis.core.service.impl;

import java.io.File;
import java.io.IOException;
import java.nio.file.Paths;
import java.util.HashMap;
import java.util.UUID;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.nio.file.Path;

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
    
    private final String OUTPUT_DIRECTORY_KEY = "outputDirectory";
    private final String UNIQUE_FOLDER_NAME_KEY = "uniqueFolderName";
    
    @Override
    public String runInnogenScript(String imagePath) throws InnogenScriptRunnerException {
        Path path = Paths.get(imagePath);
        String outputParentFolderPath = path.getParent().toString();
        HashMap<String, String> outputDirectoryResult = getOutputDirectoryForImage(outputParentFolderPath);
        String[] dockerCommand = new String[]{
                propertiesManager.getProperty(Properties.DOCKER_PATH),
                "run",
                "--mount",
                "type=bind,source=" + propertiesManager.getProperty(Properties.BASE_DIRECTORY) + ",target=" + propertiesManager.getProperty(Properties.TARGET_FOLDER),
                "extract_imgs",
                "-f",
                imagePath.substring(imagePath.indexOf("/data")).toString(),
                "-o",
                outputDirectoryResult.get(OUTPUT_DIRECTORY_KEY)
        };
        try {
            Process process = Runtime.getRuntime().exec(dockerCommand);
            process.waitFor();
        } catch (IOException | InterruptedException e) {
            throw new InnogenScriptRunnerException("Could not execute docker command for " + imagePath, e);
        }
        return outputDirectoryResult.get(UNIQUE_FOLDER_NAME_KEY);
    }
    
    private HashMap<String, String> getOutputDirectoryForImage(String outputParentFolderPath) {
        String uniqueFolderName = UUID.randomUUID().toString();
        String path =  outputParentFolderPath + File.separator + propertiesManager.getProperty(Properties.EXTRACTED_FOLDER) + File.separator + uniqueFolderName;
        File dirFile = new File(path);
        if (!dirFile.exists()) {
            dirFile.mkdirs();
        }
        HashMap<String, String> result = new HashMap<>();
        result.put(OUTPUT_DIRECTORY_KEY, path.substring(path.indexOf(propertiesManager.getProperty(Properties.TARGET_FOLDER))));
        result.put(UNIQUE_FOLDER_NAME_KEY, uniqueFolderName);
        return result;
    }

}

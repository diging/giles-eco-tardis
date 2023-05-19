package edu.asu.diging.tardis.core.service.impl;

import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;

import javax.imageio.ImageIO;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import edu.asu.diging.gilesecosystem.requests.ICompletedStorageRequest;
import edu.asu.diging.gilesecosystem.util.files.IFileStorageManager;
import edu.asu.diging.tardis.core.service.IImageProcessor;

@Component
public class ImageProcessor implements IImageProcessor {
    
    @Autowired
    private IFileStorageManager storageManager;
    
    @Override
    public String saveImageFile(BufferedImage imageFile, ICompletedStorageRequest request) throws IOException {
        String dirFolder = storageManager.getAndCreateStoragePath(request.getUsername(), request.getUploadId(), request.getDocumentId());
        String filename = request.getFilename();
        File file = new File(dirFolder + File.separator + filename);
        ImageIO.write(imageFile,"PNG", file);

        return file.getAbsolutePath();
    }
}

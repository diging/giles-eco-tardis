package edu.asu.diging.tardis.core.service.impl;

import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;

import javax.imageio.ImageIO;

import edu.asu.diging.gilesecosystem.requests.ICompletedStorageRequest;
import edu.asu.diging.gilesecosystem.util.files.IFileStorageManager;

public class ImageProcessor {
    private String dirFolder;
    
    public ImageProcessor(IFileStorageManager storageManager, ICompletedStorageRequest request) {
        dirFolder = storageManager.getAndCreateStoragePath(request.getUsername(), request.getUploadId(), request.getDocumentId());
    }
    
    protected String saveImageFile(BufferedImage imageFile, ICompletedStorageRequest request) throws IOException {
        String filename = request.getFilename();
        File file = new File(dirFolder + File.separator + filename);
        ImageIO.write(imageFile,"PNG", file);

        return file.getAbsolutePath();
    }
}

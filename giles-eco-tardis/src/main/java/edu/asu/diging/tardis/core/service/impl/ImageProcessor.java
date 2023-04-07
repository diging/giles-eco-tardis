package edu.asu.diging.tardis.core.service.impl;

import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.List;

import javax.imageio.ImageIO;

import edu.asu.diging.gilesecosystem.requests.ICompletedStorageRequest;
import edu.asu.diging.gilesecosystem.util.files.IFileStorageManager;

public class ImageProcessor {
    private String dirFolder;
    
    public ImageProcessor(IFileStorageManager storageManager, ICompletedStorageRequest request) {
        dirFolder = storageManager.getAndCreateStoragePath(request.getRequestId(), request.getDocumentId(), null);
    }
    
    protected String saveImageFile(BufferedImage imageFile, ICompletedStorageRequest request) throws IOException {
        String filename = request.getFilename() + "_" + request.getFileId();
        File file = new File(dirFolder + File.separator + filename);
        ImageIO.write(imageFile,"PNG", file);

        return file.getAbsolutePath();
    }
}
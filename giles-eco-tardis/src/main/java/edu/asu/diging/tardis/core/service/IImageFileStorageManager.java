package edu.asu.diging.tardis.core.service;

import java.awt.image.BufferedImage;
import java.io.IOException;

import edu.asu.diging.gilesecosystem.requests.ICompletedStorageRequest;

public interface IImageFileStorageManager {
    public String saveImageFile(BufferedImage imageFile, ICompletedStorageRequest request) throws IOException;

    byte[] getExtractedFileContent(String username, String uploadId, String documentId, int pageNr, String filename)
            throws IOException;

    boolean deleteExtractedFile(String username, String uploadId, String documentId, int pageNr, String filename,
            boolean deleteEmptyFolders);
}

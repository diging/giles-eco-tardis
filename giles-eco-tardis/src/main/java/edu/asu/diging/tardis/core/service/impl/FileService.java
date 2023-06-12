package edu.asu.diging.tardis.core.service.impl;

import java.io.File;
import java.io.IOException;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import edu.asu.diging.gilesecosystem.requests.ICompletedStorageRequest;
import edu.asu.diging.gilesecosystem.util.files.IFileStorageManager;
import edu.asu.diging.tardis.core.service.IFileService;
import edu.asu.diging.tardis.core.service.IImageFileStorageManager;

@Service
public class FileService implements IFileService {

    @Autowired
    private IFileStorageManager fileStorageManager;
    
    @Autowired
    private IImageFileStorageManager imageFileStorageManager;
    
    /* (non-Javadoc)
     * @see edu.asu.diging.gilesecosystem.carolus.core.linnaeus.impl.IPathService#getStoragePath(edu.asu.diging.gilesecosystem.requests.ICompletedStorageRequest)
     */
    @Override
    public String getStoragePath(ICompletedStorageRequest request) {
        File storageFolder = fileStorageManager.createFolder(request.getRequestId(), null, null, request.getDocumentId());
        return storageFolder.getAbsolutePath() + File.separator + request.getFilename();
    }
    
    @Override
    public byte[] getFileContent(String userName, String uploadId, String documentId, int pageNr, String filename) throws IOException {
        return imageFileStorageManager.getExtractedFileContent(userName, uploadId, documentId, pageNr, filename);
    }
    
    @Override
    public void deleteFile(String userName, String uploadId, String documentId, int pageNr, String filename) {
        imageFileStorageManager.deleteExtractedFile(userName, uploadId, documentId, pageNr, filename, true);
    }
}

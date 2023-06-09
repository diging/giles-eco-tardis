package edu.asu.diging.tardis.core.service.impl;

import java.io.File;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import edu.asu.diging.gilesecosystem.requests.ICompletedStorageRequest;
import edu.asu.diging.gilesecosystem.util.files.IFileStorageManager;
import edu.asu.diging.gilesecosystem.util.properties.IPropertiesManager;
import edu.asu.diging.tardis.core.service.IFileService;

@Service
public class FileService implements IFileService {

    @Autowired
    private IFileStorageManager fileStorageManager;
    
    @Autowired
    private IPropertiesManager propertiesManager;
    
    /* (non-Javadoc)
     * @see edu.asu.diging.gilesecosystem.carolus.core.linnaeus.impl.IPathService#getStoragePath(edu.asu.diging.gilesecosystem.requests.ICompletedStorageRequest)
     */
    @Override
    public String getStoragePath(ICompletedStorageRequest request) {
        File storageFolder = fileStorageManager.createFolder(request.getRequestId(), null, null, request.getDocumentId());
        return storageFolder.getAbsolutePath() + File.separator + request.getFilename();
    }
    
    @Override
    public byte[] getFileContent(String requestId, String documentId, String filename) {
        return fileStorageManager.getFileContent(requestId, documentId, null, filename);
    }
    
    @Override
    public void deleteFile(String requestId, String documentId, String filename) {
        fileStorageManager.deleteFile(requestId, documentId, null, filename, true);
    }
    
}

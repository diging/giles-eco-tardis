package edu.asu.diging.tardis.core.service;

import edu.asu.diging.gilesecosystem.requests.ICompletedStorageRequest;

public interface IFileService {

    String getStoragePath(ICompletedStorageRequest request);

    byte[] getFileContent(String userName, String uploadId, String documentId, int pageNr, String filename);

    public void deleteFile(String userName, String uploadId, String documentId, int pageNr, String filename);

}

package edu.asu.diging.tardis.core.service;

import edu.asu.diging.gilesecosystem.requests.ICompletedStorageRequest;

public interface IFileService {

    String getStoragePath(ICompletedStorageRequest request);

    byte[] getFileContent(String requestId, String documentId, String filename);

    void deleteFile(String requestId, String documentId, String filename);


}
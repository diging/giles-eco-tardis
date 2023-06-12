package edu.asu.diging.tardis.core.service;

import java.io.IOException;

import edu.asu.diging.gilesecosystem.requests.ICompletedStorageRequest;
/**
 * This class handles file deletion, storage path retrieval and getting the file content
*/
public interface IFileService {
    /**
    Returns the storage path associated with the completed storage request.
    @param request The completed storage request.
    @return The storage path as a string.
    */
    String getStoragePath(ICompletedStorageRequest request);
    
    /**

    Retrieves the content of a file specified by the provided parameters.
    @param userName The name of the user who uploaded the file.
    @param uploadId The unique identifier of the upload session.
    @param documentId The identifier of the document containing the file.
    @param pageNr The page number within the document where the file is located.
    @param filename The name of the file to retrieve.
    @return The content of the file as a byte array.
     * @throws IOException 
    */
    byte[] getFileContent(String userName, String uploadId, String documentId, int pageNr, String filename) throws IOException;
    
    /**

    Deletes a file specified by the provided parameters.
    @param userName The name of the user who uploaded the file.
    @param uploadId The unique identifier of the upload session.
    @param documentId The identifier of the document containing the file.
    @param pageNr The page number within the document where the file is located.
    @param filename The name of the file to delete.
    */
    public void deleteFile(String userName, String uploadId, String documentId, int pageNr, String filename);

}

package edu.asu.diging.tardis.core.service;

import java.awt.image.BufferedImage;
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
    @param uniqueFolder The uniqueFolder the extracted images of the file is located.
    @param filename The name of the file to retrieve.
    @return The content of the file as a byte array.
     * @throws IOException 
    */
    byte[] getFileContent(String userName, String uploadId, String documentId, String uniqueFolder, String filename) throws IOException;
    
    /**

    Deletes a file specified by the provided parameters.
    @param userName The name of the user who uploaded the file.
    @param uploadId The unique identifier of the upload session.
    @param documentId The identifier of the document containing the file.
    @param uniqueFolder The uniqueFolder the extracted images of the file is located.
    @param filename The name of the file to delete.
    */
    void deleteFile(String userName, String uploadId, String documentId, String uniqueFolder, String filename);

    /**
    Saves the provided BufferedImage as an image file. The image file is saved in a unique output directory within the storage request's parent folder.
    @param imageFile The BufferedImage to be saved as an image file.
    @param request The ICompletedStorageRequest representing the storage request.
    @return The path to the saved image file.
    @throws IOException If an I/O error occurs while saving the image file.
    */
    String saveImageFile(BufferedImage imageFile, ICompletedStorageRequest request) throws IOException;

}

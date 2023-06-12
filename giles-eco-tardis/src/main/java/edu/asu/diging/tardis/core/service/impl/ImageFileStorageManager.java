package edu.asu.diging.tardis.core.service.impl;

import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;

import javax.imageio.ImageIO;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import edu.asu.diging.gilesecosystem.requests.ICompletedStorageRequest;
import edu.asu.diging.gilesecosystem.util.files.IFileStorageManager;
import edu.asu.diging.gilesecosystem.util.properties.IPropertiesManager;
import edu.asu.diging.tardis.config.Properties;
import edu.asu.diging.tardis.core.service.IImageFileStorageManager;

@Component
public class ImageFileStorageManager implements IImageFileStorageManager {
    
    @Autowired
    private IFileStorageManager storageManager;
    
    @Autowired
    private IPropertiesManager propertiesManager;
    
    @Override
    public String saveImageFile(BufferedImage imageFile, ICompletedStorageRequest request) throws IOException {
        String dirFolder = storageManager.getAndCreateStoragePath(request.getUsername(), request.getUploadId(), request.getDocumentId());
        String filename = request.getFilename();
        File file = new File(dirFolder + File.separator + filename);
        ImageIO.write(imageFile,"PNG", file);

        return file.getAbsolutePath();
    }
    
    @Override
    public byte[] getExtractedFileContent(String username, String uploadId, String documentId, int pageNr, String filename) throws IOException {
        String folderPath = storageManager.getAndCreateStoragePath(username, uploadId, documentId);
        File fileObject = new File(folderPath + File.separator + propertiesManager.getProperty(Properties.EXTRACTED_FOLDER) + File.separator + pageNr + File.separator + propertiesManager.getProperty(Properties.EXTRACTED_FOLDER) + File.separator + filename);
        return storageManager.getFileContentFromUrl(fileObject.toURI().toURL());
    }
    
    @Override
    public boolean deleteExtractedFile(String username, String uploadId, String documentId, int pageNr,
            String filename, boolean deleteEmptyFolders) {
        String folderPath = storageManager.getAndCreateStoragePath(username, uploadId, documentId);
        File file = new File(folderPath + File.separator + propertiesManager.getProperty(Properties.EXTRACTED_FOLDER) + File.separator + pageNr + File.separator + propertiesManager.getProperty(Properties.EXTRACTED_FOLDER) + File.separator + filename);

        if (file.exists()) {
            file.delete();
        }
        if (deleteEmptyFolders) {
            boolean deletedDocFolder = deleteExtractedFolderForPage(folderPath, pageNr);
            boolean deletedPageNrFolder = deletePageNrFolder(folderPath, pageNr, deletedDocFolder);
            boolean deleteExtractedFolder = deleteExtractedFolder(folderPath, deletedPageNrFolder);
            boolean deleteDownloadFolder = deleteDocumentFolder(folderPath, deleteExtractedFolder);
            deleteUploadFolder(username, uploadId, deleteDownloadFolder);
        }

        return true;
    }
    
    private boolean deleteExtractedFolderForPage(String folderPath, int pageNr) {
        File docFolder = new File(folderPath + File.separator + propertiesManager.getProperty(Properties.EXTRACTED_FOLDER) + File.separator + pageNr + File.separator + propertiesManager.getProperty(Properties.EXTRACTED_FOLDER));
        if (docFolder.isDirectory() && docFolder.list().length == 0) {
            return docFolder.delete();
        }
        return !docFolder.exists();
    }

    private boolean deletePageNrFolder(String folderPath, int pageNr, boolean extractedFolderDeleted) {
        if (!extractedFolderDeleted) {
            return false;
        }
        File pageNrFolder = new File(folderPath + File.separator + propertiesManager.getProperty(Properties.EXTRACTED_FOLDER) + File.separator + pageNr);
        deleteFilesFromFolder(pageNrFolder);
        if (pageNrFolder.exists() && pageNrFolder.list().length == 0) {
            return pageNrFolder.delete();
        }
        return !pageNrFolder.exists();
    }

    private boolean deleteExtractedFolder(String folderPath, boolean pageNrFolderDeleted) {
        if (!pageNrFolderDeleted) {
            return false;
        }
        File extractedFolder = new File(folderPath + File.separator + propertiesManager.getProperty(Properties.EXTRACTED_FOLDER));
        if (extractedFolder.exists() && extractedFolder.list().length == 0) {
            return extractedFolder.delete();
        }
        return !extractedFolder.exists();
    }

    private boolean deleteDocumentFolder(String folderPath, boolean extractedFolderDeleted) {
        if (!extractedFolderDeleted) {
            return false;
        }
        File documentFolder = new File(folderPath);
        if (documentFolder.exists()) {
            deleteFilesFromFolder(documentFolder);
            return documentFolder.delete();
        }
        return !documentFolder.exists();
    }

    private boolean deleteUploadFolder(String username, String uploadId, boolean documentFolderDeleted) {
        if (!documentFolderDeleted) {
            return false;
        }
        File uploadFolder = new File(storageManager.getBaseDirectory() + File.separator
                + storageManager.getFileFolderPathInBaseFolder(username, uploadId, null));
        if (uploadFolder.exists() && uploadFolder.list().length == 0) {
            return uploadFolder.delete();
        }
        return !uploadFolder.exists();
    }

    private void deleteFilesFromFolder(File folder) {
        File[] files = folder.listFiles();
        if (files != null) {
            for (File file : files) {
                file.delete();
            }
        }
    }
}

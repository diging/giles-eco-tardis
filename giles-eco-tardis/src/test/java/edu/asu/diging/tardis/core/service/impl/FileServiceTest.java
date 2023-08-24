package edu.asu.diging.tardis.core.service.impl;

import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;

import javax.imageio.ImageIO;

import org.junit.Before;
import org.junit.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;

import edu.asu.diging.gilesecosystem.requests.ICompletedStorageRequest;
import edu.asu.diging.gilesecosystem.requests.impl.CompletedStorageRequest;
import edu.asu.diging.gilesecosystem.util.files.IFileStorageManager;
import edu.asu.diging.gilesecosystem.util.properties.IPropertiesManager;
import junit.framework.Assert;

public class FileServiceTest {
    @Mock
    private IFileStorageManager fileStorageManager;
    
    @Mock
    private IPropertiesManager propertiesManager;
    
    @InjectMocks
    private FileService factoryToTest = new FileService();
    
    @Before
    public void setUp() {
        MockitoAnnotations.initMocks(this);
        Mockito.when(fileStorageManager.getAndCreateStoragePath(Mockito.anyString(), Mockito.anyString(), Mockito.anyString())).thenReturn("/Users/diyabiju/Desktop/Dig/giles-eco-tardis/data/files/github_37469232/UPm7aOuNnorJYY/DOCV015oTJbefgG");
        Mockito.when(propertiesManager.getProperty(Mockito.anyString())).thenReturn("extracted");
    }
    
    @Test
    public void test_getFileContent_success() throws IOException {
        byte[] expectedResult = new byte[0];
        Mockito.when(fileStorageManager.getFileContentFromUrl(Mockito.any())).thenReturn(expectedResult);
        byte[] result = factoryToTest.getFileContent("github_37469232", "UPPzI36a0QHiRF", "DOCYe3yl6zWuYFX", "1", "HW3-DiyaBiju.pdf.1.tiff");
        Assert.assertEquals(expectedResult, result);
    }
    
    @Test(expected = IOException.class)
    public void test_getFileContent_throwsIOException() throws IOException {
        Mockito.when(fileStorageManager.getFileContentFromUrl(Mockito.any())).thenThrow(IOException.class);
        factoryToTest.getFileContent("github_37469232", "UPPzI36a0QHiRF", "DOCYe3yl6zWuYFX", "1", "HW3-DiyaBiju.pdf.1.tiff");
    }
    
    @Test
    public void test_deleteFile_success() throws IOException {
        Mockito.when(fileStorageManager.getAndCreateStoragePath(Mockito.anyString(), Mockito.anyString(), Mockito.anyString())).thenReturn("test/files/github_37469232/UPPzI36a0QHiRF/DOCYe3yl6zWuYFX");
        Mockito.when(fileStorageManager.getBaseDirectory()).thenReturn("test");
        Mockito.when(fileStorageManager.getFileFolderPathInBaseFolder(Mockito.anyString(), Mockito.anyString(), Mockito.anyString())).thenReturn("files/github_37469232/UPPzI36a0QHiRF");
        createDirectoryAndFile("test/files/github_37469232/UPPzI36a0QHiRF/DOCYe3yl6zWuYFX/extracted/1/extracted", "HW3-DiyaBiju.pdf.1.tiff");
        File file = new File("test/files/github_37469232/UPPzI36a0QHiRF/DOCYe3yl6zWuYFX/extracted/1/extracted/HW3-DiyaBiju.pdf.1.tiff");
        factoryToTest.deleteFile("github_37469232", "UPPzI36a0QHiRF", "DOCYe3yl6zWuYFX", "1", "HW3-DiyaBiju.pdf.1.tiff");
        Assert.assertFalse(file.exists());
        cleanUpFiles();
    }
    
    private void createDirectoryAndFile(String path, String fileName) throws IOException {
        File folder = new File(path);
        folder.mkdirs();
        File file = new File(folder, fileName);
        file.createNewFile();
    }
    
    private void cleanUpFiles() {
        File filesDirectory = new File("test/files");
        deleteDirectory(filesDirectory);
        File testDirectory = new File("test");
        deleteDirectory(testDirectory);
    }
    
    public void deleteDirectory(File directory) {
        if (directory.exists()) {
            File[] files = directory.listFiles();
            if (files != null) {
                for (File file : files) {
                    if (file.isDirectory()) {
                        deleteDirectory(file);
                    } else {
                        file.delete();
                    }
                }
            }
            directory.delete();
        }
    }
}

package edu.asu.diging.tardis.core.service.impl;

import java.io.IOException;

import org.junit.Before;
import org.junit.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;

import edu.asu.diging.gilesecosystem.util.files.IFileStorageManager;
import edu.asu.diging.tardis.core.service.IImageFileStorageManager;
import junit.framework.Assert;

public class FileServiceTest {
    @Mock
    private IFileStorageManager fileStorageManager;
    
    @Mock
    private IImageFileStorageManager imageFileStorageManager;
    
    @InjectMocks
    private FileService factoryToTest = new FileService();
    
    @Before
    public void setUp() {
        MockitoAnnotations.initMocks(this);
    }
    
    @Test
    public void test_getFileContent_success() throws IOException {
        byte[] expectedResult = new byte[0];
        Mockito.when(imageFileStorageManager.getExtractedFileContent(Mockito.anyString(), Mockito.anyString(), Mockito.anyString(), Mockito.anyInt(), Mockito.anyString())).thenReturn(expectedResult);
        byte[] result = factoryToTest.getFileContent("github_37469232", "UPPzI36a0QHiRF", "DOCYe3yl6zWuYFX", 1, "HW3-DiyaBiju.pdf.1.tiff");
        Assert.assertEquals(expectedResult, result);
        Mockito.verify(imageFileStorageManager, Mockito.times(1)).getExtractedFileContent("github_37469232", "UPPzI36a0QHiRF", "DOCYe3yl6zWuYFX", 1, "HW3-DiyaBiju.pdf.1.tiff");
    }
    
    @Test(expected = IOException.class)
    public void test_getFileContent_throwsIOException() throws IOException {
        Mockito.when(imageFileStorageManager.getExtractedFileContent(Mockito.anyString(), Mockito.anyString(), Mockito.anyString(), Mockito.anyInt(), Mockito.anyString())).thenThrow(new IOException());
        factoryToTest.getFileContent("github_37469232", "UPPzI36a0QHiRF", "DOCYe3yl6zWuYFX", 1, "HW3-DiyaBiju.pdf.1.tiff");
    }
    
    @Test
    public void test_deleteFile_success() {
        factoryToTest.deleteFile("github_37469232", "UPPzI36a0QHiRF", "DOCYe3yl6zWuYFX", 1, "HW3-DiyaBiju.pdf.1.tiff");
        Mockito.verify(imageFileStorageManager, Mockito.times(1)).deleteExtractedFile("github_37469232", "UPPzI36a0QHiRF", "DOCYe3yl6zWuYFX", 1, "HW3-DiyaBiju.pdf.1.tiff", true);
    }
}

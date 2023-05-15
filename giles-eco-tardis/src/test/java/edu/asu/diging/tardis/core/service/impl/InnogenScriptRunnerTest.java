package edu.asu.diging.tardis.core.service.impl;

import static org.junit.Assert.assertThrows;

import java.io.IOException;

import org.junit.Before;
import org.junit.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;

import edu.asu.diging.gilesecosystem.septemberutil.service.ISystemMessageHandler;
import edu.asu.diging.gilesecosystem.util.properties.IPropertiesManager;
import edu.asu.diging.tardis.config.Properties;

public class InnogenScriptRunnerTest {
    
    @Mock
    protected ISystemMessageHandler messageHandler;
    
    @Mock
    protected IPropertiesManager propertiesManager;
    
    @Mock
    private Runtime runtimeMock;

    @Mock
    private Process processMock;
    
    @InjectMocks
    private InnogenScriptRunner factoryToTest = new InnogenScriptRunner();
    
    
    @Before
    public void setUp() {
        MockitoAnnotations.initMocks(this);
        Mockito.when(propertiesManager.getProperty(Properties.DOCKER_PATH)).thenReturn("/usr/local/bin/docker");
    }
    
    @Test
    public void test_runInnogenScript_success() throws IOException, InterruptedException {
        Mockito.when(runtimeMock.exec(Mockito.anyString())).thenReturn(processMock);
        Mockito.when(processMock.waitFor()).thenReturn(0);
        String[] dockerCommand = new String[]{
                propertiesManager.getProperty(Properties.DOCKER_PATH),
                "run",
                "--mount",
                "type=bind,source=" + propertiesManager.getProperty(Properties.BASE_DIRECTORY) + ",target=/data",
                "extract_imgs",
                "-f",
                "Users/dabiju/giles-eco-tardis/data/files/github_37469232/UPPzI36a0QHiRF/DOCYe3yl6zWuYFX/HW3-DiyaBiju.pdf.1.tiff".substring("Users/dabiju/giles-eco-tardis/data/files/github_37469232/UPPzI36a0QHiRF/DOCYe3yl6zWuYFX/HW3-DiyaBiju.pdf.1.tiff".indexOf("/data")).toString(),
                "-o",
                "files/github_37469232/UPPzI36a0QHiRF/DOCYe3yl6zWuYFX/extracted/1"
        };
        factoryToTest.runInnogenScript("Users/dabiju/giles-eco-tardis/data/files/github_37469232/UPPzI36a0QHiRF/DOCYe3yl6zWuYFX/HW3-DiyaBiju.pdf.1.tiff", 1);
        Mockito.verify(runtimeMock, Mockito.times(1)).exec(dockerCommand);
    }
    
    @Test
    public void test_runInnogenScript_throwsIOEXception() throws IOException, InterruptedException {
        String[] dockerCommand = new String[]{
                propertiesManager.getProperty(Properties.DOCKER_PATH),
                "run",
                "--mount",
                "type=bind,source=" + propertiesManager.getProperty(Properties.BASE_DIRECTORY) + ",target=/data",
                "extract_imgs",
                "-f",
                "Users/dabiju/giles-eco-tardis/data/files/github_37469232/UPPzI36a0QHiRF/DOCYe3yl6zWuYFX/HW3-DiyaBiju.pdf.1.tiff".substring("Users/dabiju/giles-eco-tardis/data/files/github_37469232/UPPzI36a0QHiRF/DOCYe3yl6zWuYFX/HW3-DiyaBiju.pdf.1.tiff".indexOf("/data")).toString(),
                "-o",
                "files/github_37469232/UPPzI36a0QHiRF/DOCYe3yl6zWuYFX/extracted/1"
        };
        Mockito.when(runtimeMock.exec(dockerCommand)).thenReturn(processMock);
        Mockito.when(processMock.waitFor()).thenThrow(new IOException());
        factoryToTest.runInnogenScript("Users/dabiju/giles-eco-tardis/data/files/github_37469232/UPPzI36a0QHiRF/DOCYe3yl6zWuYFX/HW3-DiyaBiju.pdf.1.tiff", 1);
        assertThrows(IOException.class, () -> factoryToTest.runInnogenScript("Users/dabiju/giles-eco-tardis/data/files/github_37469232/UPPzI36a0QHiRF/DOCYe3yl6zWuYFX/HW3-DiyaBiju.pdf.1.tiff", 1));
    }
    
}

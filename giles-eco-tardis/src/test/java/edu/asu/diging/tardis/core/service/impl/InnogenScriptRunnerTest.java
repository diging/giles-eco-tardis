package edu.asu.diging.tardis.core.service.impl;

import java.io.File;
import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

import org.junit.Assert;
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
    private Process processMock;
    
    @Mock
    private File dirFileMock;
    
    @InjectMocks
    private InnogenScriptRunner factoryToTest = new InnogenScriptRunner();
    
    @Before
    public void setUp() throws IOException {
        MockitoAnnotations.initMocks(this);
        Mockito.when(propertiesManager.getProperty(Properties.DOCKER_PATH)).thenReturn("/usr/local/bin/docker");
        Mockito.when(dirFileMock.exists()).thenReturn(false);
    }
    
    @Test
    public void test_getOutputDirectoryForImage_success() throws NoSuchMethodException, SecurityException, IllegalAccessException, IllegalArgumentException, InvocationTargetException {
        Method getOutputDirectoryForImage = InnogenScriptRunner.class.getDeclaredMethod("getOutputDirectoryForImage", String.class, int.class);
        getOutputDirectoryForImage.setAccessible(true);
        String outputDirectory = (String) getOutputDirectoryForImage.invoke(factoryToTest, "Users/dabiju/giles-eco-tardis/data/files/github_37469232/UPPzI36a0QHiRF/DOCYe3yl6zWuYFX", 1);
        Assert.assertEquals("/data/files/github_37469232/UPPzI36a0QHiRF/DOCYe3yl6zWuYFX/extracted/1", outputDirectory);
    }
}

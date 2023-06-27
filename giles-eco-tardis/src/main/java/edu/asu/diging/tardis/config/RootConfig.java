package edu.asu.diging.tardis.config;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.EnableAspectJAutoProxy;
import org.springframework.context.annotation.PropertySource;
import org.springframework.context.support.PropertySourcesPlaceholderConfigurer;
import org.springframework.context.support.ReloadableResourceBundleMessageSource;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.servlet.config.annotation.EnableWebMvc;

import edu.asu.diging.gilesecosystem.septemberutil.service.ISystemMessageHandler;
import edu.asu.diging.gilesecosystem.septemberutil.service.impl.SystemMessageHandler;
import edu.asu.diging.gilesecosystem.util.files.IFileStorageManager;
import edu.asu.diging.gilesecosystem.util.files.impl.FileStorageManager;
import edu.asu.diging.gilesecosystem.util.properties.IPropertiesManager;

@Configuration
@EnableWebMvc
@EnableAspectJAutoProxy
@ComponentScan({ "edu.asu.diging.tardis",
        "edu.asu.diging.simpleusers.core",
        "edu.asu.diging.gilesecosystem.util.properties",
        "edu.asu.diging.gilesecosystem.requests" })
@PropertySource("classpath:config.properties")
public class RootConfig {

    @Value("${base_directory}")
    private String baseDirectory;
    
    @Value("${file_folder}")
    private String fileFolder;
    
    @Autowired
    private IPropertiesManager propertyManager;
    
    @Bean
    public static PropertySourcesPlaceholderConfigurer propertyConfigInDev() {
        return new PropertySourcesPlaceholderConfigurer();
    }

    @Bean
    public ISystemMessageHandler getMessageHandler() {
        return new SystemMessageHandler(
                propertyManager.getProperty(Properties.APPLICATION_ID));
    }

    @Bean
    public ReloadableResourceBundleMessageSource messageSource() {
        ReloadableResourceBundleMessageSource source = new ReloadableResourceBundleMessageSource();
        source.setBasename("classpath:locale/messages");
        source.setFallbackToSystemLocale(false);
        return source;
    }
    
    @Bean
    public IFileStorageManager fileStorageManager() {
        FileStorageManager storageManager = new FileStorageManager();
        storageManager.setBaseDirectory(baseDirectory);
        storageManager.setFileTypeFolder(fileFolder);
        return storageManager;
    }
    
    @Bean
    public RestTemplate restTemplate() {
        return new RestTemplate();
    }
}

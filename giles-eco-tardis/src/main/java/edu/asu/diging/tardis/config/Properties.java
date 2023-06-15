package edu.asu.diging.tardis.config;


public interface Properties {

    public final static String APP_BASE_URL = "app_base_url";
    public final static String GILES_ACCESS_TOKEN = "giles_access_token";
    public final static String KAFKA_TOPIC_STORAGE_COMPLETE = "topic_storage_request_complete";
    
    public final static String KAFKA_HOSTS = "kafka_hosts";
    public final static String KAFKA_TOPIC_OCR_REQUEST = "request_ocr_topic";
    public final static String KAFKA_TOPIC_OCR_COMPLETE_REQUEST = "topic_orc_request_complete";
    public final static String KAFKA_TOPIC_STORAGE_REQUEST = "request_storage_topic";
    public final static String KAFKA_TOPIC_STORAGE_COMPLETE_REQUEST = "topic_storage_request_complete";
    public final static String KAFKA_TOPIC_TEXT_EXTRACTION_REQUEST = "request_text_extraction_topic";
    public final static String KAFKA_TOPIC_TEXT_EXTRACTION_COMPLETE_REQUEST = "topic_text_extraction_request_complete";
    public final static String KAFKA_TOPIC_IMAGE_EXTRACTION_REQUEST = "topic_image_extraction_request";
    public final static String KAFKA_TOPIC_IMAGE_EXTRACTION_COMPLETE_REQUEST = "topic_image_extraction_request_complete";
    public final static String KAFKA_TOPIC_SYSTEM_MESSAGES = "topic_system_messages";
    public final static String KAFKA_TOPIC_COMPLETION_NOTIFICATIION = "topic_completion_notification";
    
    public final static String APPLICATION_ID = "application_id";
    
    public final static String ZOOKEEPER_HOST = "zookeeper_host";
    public final static String ZOOKEEPER_PORT = "zookeeper_port";
    
    public final static String ZOOKEEPER_NEPOMUK_SERVICE_NAME = "zookeeepr_service_nepomuk_name";
    public final static String ZOOKEEPER_SERVICE_ROOT = "zookeeper_service_root";
    
    public final static String APP_URL = "app_url";
    public final static String NOTIFIER_ID = "tardis_notifier_id";

    public final static String BASE_DIRECTORY = "base_directory";
    public final static String DOCKER_PATH = "docker_path";
    public final static String EXTRACTED_FOLDER = "extracted_folder";
}

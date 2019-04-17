package com.telegram.bot;

import java.io.FileInputStream;
import java.io.IOException;
import java.util.Properties;

/**
 * @author Egor
 * Класс для получение проперти из файлов *.properties
 */
class PropertiesService {
   private String fileLocation;
   private Properties properties;

    public String getFileLocation() {
        return fileLocation;
    }

    public void setFileLocation(String fileLocation) {
        this.fileLocation = fileLocation;
    }

    Properties getProperties() {
        return properties;
    }

    public void setProperties(Properties properties) {
        this.properties = properties;
    }

    PropertiesService(String filePath) {
        fileLocation = filePath;
        properties = getProperties(fileLocation);
    }

    /**
     * Получает данные из файла properties
     * @return объект со значениями свойств
     */
    private Properties getProperties(String filePath){
        Properties properties = new Properties();
        try {
            properties.load(new FileInputStream(filePath));
        } catch (IOException e) {
            e.printStackTrace();
        }
        return properties;
    }

    /**
     * Возвращает проперти по имени
     * @param propName имя проперти
     * @return строка со значением
     */
    private String getProperty(String propName){
        Properties properties = getProperties(propName);
        return properties.getProperty(propName);
    }
}

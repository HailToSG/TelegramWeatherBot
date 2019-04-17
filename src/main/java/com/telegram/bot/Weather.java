package com.telegram.bot;

import java.net.MalformedURLException;
import java.net.URL;
import java.util.Properties;

public class Weather {
    private Properties tokens =
            new PropertiesService("tokens.properties")
                    .getProperties();
    public static String getWeather(String message, Model Model) throws MalformedURLException {
        URL url = new URL("ds");
        return null;
    }
}

package com.uclab.everytree.services;

public class AppConfig {
    private static final Integer MAX_COUNT_PHOTOS = 10; //максимальное количество фото к загрузке
    private static final String BASE_URL = "https://eco.urbanbasis.com"; //10.0.2.2 "http://192.168.1.105:9595"
    private static final String dateFormat = "dd.MM.yyyy";

    public static String getDateFormat()
    {
        return dateFormat;
    }

    public static Integer getMaxCountPhotos()
    {
        return MAX_COUNT_PHOTOS;
    }

    public static String getBaseUrl()
    {
        return BASE_URL;
    }
}

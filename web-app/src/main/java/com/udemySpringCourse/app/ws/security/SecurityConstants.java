package com.udemySpringCourse.app.ws.security;

import com.udemySpringCourse.app.ws.SpringApplicationContext;

public class SecurityConstants {
    protected static final long EXPIRATION_TIME = 864000000; //10 days
    public static final String TOKEN_PREFIX = "Bearer ";
    public static final String HEADER_STRING = "Authorization";
    public static final String SIGN_UP_URL = "/users";


    public static String getTokenSecret() {
        //ATTENTION bean name was uppercase giving error
        AppProperties appProperties = (AppProperties) SpringApplicationContext.getBean("AppProperties");
        return appProperties.getTokenSecret();
    }

}

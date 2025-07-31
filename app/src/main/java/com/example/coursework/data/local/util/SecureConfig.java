package com.example.coursework.data.local.util;

import com.example.coursework.BuildConfig;


public class SecureConfig {

    public static String getFirebaseApiKey() {
        return BuildConfig.FIREBASE_API_KEY;
    }
    

    public static String getFirebaseProjectId() {
        return BuildConfig.FIREBASE_PROJECT_ID;
    }
    

    public static String getFirebaseProjectNumber() {
        return BuildConfig.FIREBASE_PROJECT_NUMBER;
    }

    public static String getFirebaseMobileSdkAppId() {
        return BuildConfig.FIREBASE_MOBILE_SDK_APP_ID;
    }
    

    public static boolean isFirebaseConfigured() {
        return true;
    }
}
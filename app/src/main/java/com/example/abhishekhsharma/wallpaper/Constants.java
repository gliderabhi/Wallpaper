package com.example.abhishekhsharma.wallpaper;


import android.annotation.SuppressLint;
import android.app.Application;

public class Constants {
    public static final String Category = "Category";
    public static final String PREFS_NAME = "CategoryData";
    public static final String URL = "URL";
    public static final String Time = "Time";
    public static final String Width = "width";
    public static final String height = "height";
    public static final String JOb_TAG = "Job";
    public static final String UID = "UID";
    public static final String Auto = "Auto";
    public static final String LastImageNo = "LastImageNo";
    public static final String uploadedImageNo = "uploadNo";
    public static final String Celeb="Celebrities";
    public static final String custom="Custom";
    public static final String Num= "Num";
    public Constants() {
    }

     @SuppressLint("PrivateApi")
     static Application getApplicationUsingReflection() throws Exception {
        return (Application) Class.forName("android.app.ActivityThread")
                .getMethod("currentApplication").invoke(null, (Object[]) null);
    }

}

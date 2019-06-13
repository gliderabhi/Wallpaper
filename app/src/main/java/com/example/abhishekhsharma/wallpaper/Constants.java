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
    public static final String space="Space";
    public static final String cars="Cars";
    public static final String building="Buildings";
    public static final String nature="Nature";
    public static final String ocean="Ocean";
    public static final String custom="Custom";

    public static final String CelebUrl="https://firebasestorage.googleapis.com/v0/b/wallpaper-5853e.appspot.com/o/imageUrl%2Fcelebrity.jpg?alt=media&token=da209a81-70e8-467b-b61d-95db0ce61b43";
    public static final String spaceUrl="https://firebasestorage.googleapis.com/v0/b/wallpaper-5853e.appspot.com/o/imageUrl%2Fspace.jpg?alt=media&token=127329f8-65dc-46df-80c6-56b4f87f2cee";
    public static final String carsUrl="https://firebasestorage.googleapis.com/v0/b/wallpaper-5853e.appspot.com/o/imageUrl%2Fcars.jpg?alt=media&token=1690c14d-fbfd-4759-a8f9-a11bdbd1f413";
    public static final String buildingUrl="https://firebasestorage.googleapis.com/v0/b/wallpaper-5853e.appspot.com/o/imageUrl%2Fbuildings.jpg?alt=media&token=1014474c-f2cb-43d2-ae3d-378264b3169f";
    public static final String natureUrl="https://firebasestorage.googleapis.com/v0/b/wallpaper-5853e.appspot.com/o/imageUrl%2Fnature.jpg?alt=media&token=04eadbfc-c37d-48b1-91a6-e2cb556beffa";
    public static final String oceanUrl="https://firebasestorage.googleapis.com/v0/b/wallpaper-5853e.appspot.com/o/imageUrl%2Focean.jpg?alt=media&token=33a13b8e-d967-4a26-9c7a-9cd808b0d980";
    public static final String customUrl="https://firebasestorage.googleapis.com/v0/b/wallpaper-5853e.appspot.com/o/imageUrl%2Fcustom.jpg?alt=media&token=35404516-9d3a-4ad8-b7f1-59019817ac46";

    public Constants() {
    }

     @SuppressLint("PrivateApi")
     static Application getApplicationUsingReflection() throws Exception {
        return (Application) Class.forName("android.app.ActivityThread")
                .getMethod("currentApplication").invoke(null, (Object[]) null);
    }

}

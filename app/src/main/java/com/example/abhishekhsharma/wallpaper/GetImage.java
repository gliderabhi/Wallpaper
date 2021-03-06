package com.example.abhishekhsharma.wallpaper;

import android.app.WallpaperManager;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.target.CustomTarget;
import com.bumptech.glide.request.transition.Transition;
import com.firebase.jobdispatcher.JobParameters;

import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.os.Handler;
import android.os.PowerManager;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.firebase.jobdispatcher.JobService;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ServerValue;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Target;

import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

public class GetImage extends JobService {
    private String category ;
    private SharedPreferences preferences;
    private WallpaperManager myWallpaperManager;
    private Boolean finished = false ;
    private  boolean OnOff;
    private JobParameters parameter;
    private void doInBack(JobParameters parameters) {
        parameter = parameters;
        new Thread(() -> {
            try {
                preferences = Constants.getApplicationUsingReflection().getApplicationContext().getSharedPreferences(Constants.PREFS_NAME, MODE_PRIVATE);
            } catch (Exception e) {
                jobFinished(parameters, false);
                e.printStackTrace();
            }


            if (preferences != null) {
                category = preferences.getString(Constants.Category, "");

                if (!category.matches("Custom")) {

                    getLastNode();

            }else{
                   downloadImageCustom();

                }
        }


            //jobFinished(parameters, false);
        }).start();
    }

    private void getLastNode(){
        final int[] node = new int[1];
        //Log.e( "Msg","Getting image no" );
        FirebaseDatabase mFirebase= FirebaseDatabase.getInstance();
        DatabaseReference mdata= mFirebase.getReference("count");
        mdata.addValueEventListener( new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                //Log.e("msg", String.valueOf( dataSnapshot.getValue()) );
                Count cn= dataSnapshot.getValue(Count.class);
                switch (category) {
                    case "Celebrities":
                        node[0] = Objects.requireNonNull(cn).getCeleb();
                        break;
                    case "Cars":
                        node[0] = Objects.requireNonNull(cn).getCars();
                        break;
                    case "Space":
                        node[0] = Objects.requireNonNull(cn).getSpace();
                        break;
                    case "Nature":
                        node[0] = Objects.requireNonNull(cn).getNature();
                        break;
                    case "Buildings":
                        node[0] = Objects.requireNonNull(cn).getBuilding();
                        break;
                    case "Ocean":
                        node[0] = Objects.requireNonNull(cn).getOcean();
                        break;
                }

                //Log.e( "msg",category+ " "+ String.valueOf( node[0] ));

                DownloadCategoryImage( node[0] );
                  }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        } );
    }
    private void DownloadCategoryImage(int nde){
        //Log.e( "Msg","Entered category dwnload "+String.valueOf( nde ) );
        int imageCatNO=getRandomNumberInRange(nde );

        FirebaseDatabase mFirebase= FirebaseDatabase.getInstance();
        DatabaseReference mdata= mFirebase.getReference(category);
        Query q= mdata.orderByChild( "name" ).equalTo( String.valueOf( imageCatNO )+ ".jpg" );
        q.addValueEventListener( new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                for(DataSnapshot ds : dataSnapshot.getChildren()){
                    Upload upload= ds.getValue(Upload.class);
                    Log.e( "msg",upload.getImageUrl() );
                    downloadImage( upload.getImageUrl() );
                     }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        } );

    }

    private String deviceAppUID;
    private void downloadImageCustom() {

        //Log.e( "Msg","Entered custom dwnload" );
        int imageNo=preferences.getInt(Constants.LastImageNo,1);
        int imageNoRand=getRandomNumberInRange(imageNo);
         deviceAppUID  = preferences.getString(Constants.UID,"");

        FirebaseDatabase mFirebase= FirebaseDatabase.getInstance();
        DatabaseReference mdata= mFirebase.getReference(deviceAppUID);
        Query q= mdata.orderByChild( "name" ).equalTo( String.valueOf( imageNoRand )+ ".jpg" );
        q.addValueEventListener( new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                for(DataSnapshot ds : dataSnapshot.getChildren()){
                    Upload upload= ds.getValue(Upload.class);
                    Log.e( "msg",upload.getImageUrl() );
                    downloadImage( upload.getImageUrl() );

                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        } );

    }

    private void downloadImage(String url) {
        //Log.e("Enter","entered download unit ");
        try {


         Log.e("msg", "before glide ");
            Glide.with(this)
                    .asBitmap()
                    .load(url)
                    .into(new CustomTarget<Bitmap>() {
                        @Override
                        public void onResourceReady(@NonNull Bitmap bitmap, @Nullable Transition<? super Bitmap> transition) {
                            try {
                                myWallpaperManager = WallpaperManager.getInstance( Constants.getApplicationUsingReflection().getApplicationContext() );
                            } catch (Exception e) {
                                e.printStackTrace();
                            }

                            try {
                                myWallpaperManager.setBitmap( bitmap );
                                Log.e( "textMsg", "wallpaper set" );
                                finished = true;

                                jobFinished(parameter, false);
                            } catch (IOException e) {
                                e.printStackTrace();
                            }


                        }

                        @Override
                        public void onLoadCleared(@Nullable Drawable placeholder) {
                        }
                    });

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private int getRandomNumberInRange( int end) {
        int d =preferences.getInt(Constants.Num , 1);
        if(d<=end){
            Log.e("msg",String.valueOf(d));
            SharedPreferences.Editor editor = preferences.edit();
            editor.putInt(Constants.Num, d + 1);
            editor.apply();
            return d;
        }else {

        d=1;
        }


        return 1;
    }


    @Override
    public boolean onStartJob(JobParameters job) {
        Log.e(Constants.JOb_TAG,"started");
        try {
            preferences = Constants.getApplicationUsingReflection().getApplicationContext().getSharedPreferences(Constants.PREFS_NAME, MODE_PRIVATE);
        } catch (Exception e) {
            e.printStackTrace();
        }
        PowerManager pm = (PowerManager) getSystemService(Context.POWER_SERVICE);
         OnOff = pm.isInteractive();

        if(OnOff) {
            DatabaseReference db = FirebaseDatabase.getInstance().getReference( "User" );
            Map map = new HashMap();
            Log.e( "msg", " working the job " );
            map.put( "timestamp", ServerValue.TIMESTAMP );
            map.put( "user", Objects.requireNonNull( FirebaseAuth.getInstance().getCurrentUser() ).getUid() );
            map.put( "category", Objects.requireNonNull( preferences.getString( Constants.Category, "" ) ) );
            db.child( Objects.requireNonNull( preferences.getString( Constants.UID, "" ) ) ).updateChildren( map );
            doInBack( job );
        }else{
            jobFinished(job,false);
            Log.e( "msg", " Exiting the job " );
        }
        return true;
    }
    public static void deleteCache(Context context) {
        try {
            File dir = context.getCacheDir();
            deleteDir(dir);
        } catch (Exception e) { e.printStackTrace();}
    }

    public static boolean deleteDir(File dir) {
        if (dir != null && dir.isDirectory()) {
            String[] children = dir.list();
            for (int i = 0; i < children.length; i++) {
                boolean success = deleteDir(new File(dir, children[i]));
                if (!success) {
                    return false;
                }
            }
            return dir.delete();
        } else if(dir!= null && dir.isFile()) {
            return dir.delete();
        } else {
            return false;
        }
    }
    @Override
    public boolean onStopJob(JobParameters job) {
        //Log.e(Constants.JOb_TAG,"ended");
        try {
            deleteCache( Constants.getApplicationUsingReflection().getApplicationContext() );
        } catch (Exception e) {
            e.printStackTrace();
        }
        return true;
    }


   /* public void downloadImage(){
        */
}

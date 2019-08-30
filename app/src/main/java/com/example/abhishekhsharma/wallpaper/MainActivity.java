package com.example.abhishekhsharma.wallpaper;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.content.res.Resources;
import android.graphics.Point;
import android.os.Bundle;
import android.util.Log;
import android.view.Display;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.firebase.jobdispatcher.Constraint;
import com.firebase.jobdispatcher.FirebaseJobDispatcher;
import com.firebase.jobdispatcher.GooglePlayDriver;
import com.firebase.jobdispatcher.Job;
import com.firebase.jobdispatcher.Lifetime;
import com.firebase.jobdispatcher.Trigger;
import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.squareup.picasso.Callback;
import com.squareup.picasso.Picasso;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.UUID;


public class MainActivity extends Activity {
    private SharedPreferences preferences;
    private static final String JOb_TAG="Job";
    protected FirebaseJobDispatcher dispatcher;
    private int timeInterval;
    private SharedPreferences.Editor editor;
    private String uid,url;
    private Job job;
    private ProgressDialog progress;
    public ArrayList categoryList;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        initialise();
        createView();

         if(preferences.getInt(Constants.Auto,0)==0) {
             builder();
         }
        Display display =getWindowManager().getDefaultDisplay();
        Point size = new Point();
        display.getSize(size);
        int width = size.x;
        int height = size.y;
        editor.putInt(getString(R.string.Width),width);
        editor.putInt(getString(R.string.height),height);
        editor.apply();

        if(job!=null){

            try {
                dispatcher.mustSchedule(job);
            } catch (Exception e) {
                Log.e("msg",e.getMessage());
                e.printStackTrace();
            }
        }

             }

    public void startJOb() {

                 if (!preferences.getString(Constants.Category, "").matches(Constants.custom)) {
                     startActivity(new Intent(getApplicationContext(), SetTime.class));

                      job = dispatcher.newJobBuilder()
                             .setService(GetImage.class)
                             .setLifetime(Lifetime.FOREVER)
                             .setRecurring(true)
                             .setTrigger(Trigger.executionWindow(timeInterval*30 , timeInterval * 60))
                             .setConstraints(Constraint.ON_ANY_NETWORK)
                             .setTag( JOb_TAG )

                             .build();

                     try {
                         dispatcher.mustSchedule(job);
                     } catch (Exception e) {
                         Log.e("msg",e.getMessage());
                         e.printStackTrace();
                     }
                     //scheduleJOb();

                     Log.e(JOb_TAG, "job started main");
                 }else{
                      job = dispatcher.newJobBuilder()
                             .setService(GetImage.class)
                             .setLifetime(Lifetime.FOREVER)
                             .setRecurring(true)
                             .setTrigger(Trigger.executionWindow(timeInterval*30 , timeInterval * 60))
                             .setConstraints(Constraint.ON_ANY_NETWORK)
                             .setTag( JOb_TAG )
                             .build();
                     Log.e(JOb_TAG, "job started custom");


                     try {
                         dispatcher.mustSchedule(job);
                     } catch (Exception e) {
                         Log.e("msg",e.getMessage());
                         e.printStackTrace();
                     }
                 }


             }

    public void initialise(){

                 uid= UUID.randomUUID().toString();
                 preferences = getApplicationContext().getSharedPreferences(getString(R.string.PREFS_NAME), Context.MODE_PRIVATE);


                 dispatcher=new FirebaseJobDispatcher(new GooglePlayDriver(this));
                 editor = preferences.edit();
                 if((Objects.requireNonNull(preferences.getString(Constants.UID, "")).matches(""))){
                     editor.putString(Constants.UID,uid);
                     editor.apply();
                 }
                 timeInterval= Integer.parseInt(Objects.requireNonNull(preferences.getString(getString(R.string.Time), "1")));

                 Log.e( "msg","working image set" );

                 categoryList = new ArrayList();
             }



    private void builder(){
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Notice");
        builder.setMessage("Do you want to add the app to autostart for better performance?");

        // add the buttons
        builder.setPositiveButton("Add to autosart", (dialog, which) -> {
                runAutoStart();
               editor.putInt(Constants.Auto,1);
               editor.apply();
        });
        builder.setNeutralButton("Remind me later", (dialog, which) -> {
            editor.putInt(Constants.Auto,0);
            editor.apply();
        });
        builder.setNegativeButton("Cancel", (dialog, which) -> {
            AlertDialog.Builder builder1=new AlertDialog.Builder(MainActivity.this);
            builder1.setTitle("Cancelling will not run the app properly")
                    .setPositiveButton("Continue with cancel", (dialog1, which1) -> {
                        editor.putInt(Constants.Auto,1);
                        editor.apply();
                    })
                    .setNegativeButton("Go to autosart settings? ", (dialog12, which12) -> {
                            runAutoStart();
                           editor.putInt(Constants.Auto,1);
                           editor.apply();

                    });
            AlertDialog dialog2 = builder1.create();
            dialog2.show();
            editor.putInt(Constants.Auto,1);
            editor.apply();
        });
        AlertDialog dialog = builder.create();
        dialog.show();
    }

    private void runAutoStart() {
        try {
            Intent intent = new Intent();
            String manufacturer = android.os.Build.MANUFACTURER;
            if ("xiaomi".equalsIgnoreCase(manufacturer)) {
                intent.setComponent(new ComponentName("com.miui.securitycenter", "com.miui.permcenter.autostart.AutoStartManagementActivity"));

            } else if ("oppo".equalsIgnoreCase(manufacturer)) {
                intent.setComponent(new ComponentName("com.coloros.safecenter", "com.coloros.safecenter.permission.startup.StartupAppListActivity"));

            } else if ("vivo".equalsIgnoreCase(manufacturer)) {
                intent.setComponent(new ComponentName("com.vivo.permissionmanager", "com.vivo.permissionmanager.activity.BgStartUpManagerActivity"));

            } else if ("Letv".equalsIgnoreCase(manufacturer)) {
                intent.setComponent(new ComponentName("com.letv.android.letvsafe", "com.letv.android.letvsafe.AutobootManageActivity"));

            } else if ("Honor".equalsIgnoreCase(manufacturer)) {
                intent.setComponent(new ComponentName("com.huawei.systemmanager", "com.huawei.systemmanager.optimize.process.ProtectActivity"));

            }

            List<ResolveInfo> list = getPackageManager().queryIntentActivities(intent, PackageManager.MATCH_DEFAULT_ONLY);
            if (list.size() > 0) {
                startActivity(intent);
            }
        } catch (Exception e) {
            Log.e("exc", String.valueOf(e));
        }
    }

    private FirebaseAuth mAuth;

    private void signInAnonymously() {
        mAuth.signInAnonymously().addOnSuccessListener(this, authResult -> {
            Log.e("TAG", "success sign");
            // do your stuff
        })
                .addOnFailureListener(this, exception -> Log.e("TAG", "failed sign"));
    }


    @Override
    public void onBackPressed() {
        startActivity(new Intent(getApplicationContext(),MainActivity.class));
    }

    private RecyclerView recyclerView;
    private FirebaseRecyclerAdapter<ImagesDetails,ViewHolder> adapter;

    private void createView(){

        recyclerView = findViewById(R.id.listtwo);

        progress = new ProgressDialog(this);
        progress.setMessage("Please wait !!! Loading");
        progress.setProgressStyle(ProgressDialog.STYLE_SPINNER);
        progress.setIndeterminate(true);
        progress.setProgress(0);
        progress.show();

        //linearLayoutManager = new LinearLayoutManager(this);
        GridLayoutManager gridLayoutManager = new GridLayoutManager(getApplicationContext(),3);
        gridLayoutManager.setOrientation(LinearLayoutManager.HORIZONTAL); // set Horizontal Orientation

        recyclerView.setLayoutManager(gridLayoutManager);
        //recyclerView.addItemDecoration(new SpacesItemDecoration(20));


        Query query = FirebaseDatabase.getInstance()
            .getReference()
            .child("CategoryImages");

        FirebaseRecyclerOptions<ImagesDetails> options =
                new FirebaseRecyclerOptions.Builder<ImagesDetails>()
                        .setQuery(query, snapshot -> {
                            if (snapshot != null) {

                                //Log.e( "msg","items here " );
                                return new ImagesDetails( snapshot.child( "name" ).getValue().toString(),
                                        snapshot.child( "category" ).getValue().toString(),
                                        snapshot.child( "urlImage" ).getValue().toString() );
                            }
                            else{
                                return null;
                            }
                        } )
                        .build();

         adapter = new FirebaseRecyclerAdapter<ImagesDetails, ViewHolder>(options) {
            @NonNull
            @Override
            public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
                // Create a new instance of the ViewHolder, in this case we are using a custom
                // layout called R.layout.message for each item
                View view = LayoutInflater.from(parent.getContext())
                        .inflate(R.layout.list_item, parent, false);
                return new ViewHolder(view);
            }


            @Override
            protected void onBindViewHolder(@NonNull ViewHolder holder, int position, @NonNull ImagesDetails model) {
                   holder.setImg( model.getUrlImage() );
                   holder.setTxtDesc( model.getCategory() );
                   holder.setTxtTitle( model.getName() );

                   holder.img.setOnClickListener( v -> {
                       Toast.makeText( getApplicationContext(),model.getName(),Toast.LENGTH_SHORT ).show();

                       if(model.getName().matches("People")){
                           editor.putString(Constants.Category, Constants.Celeb);editor.apply();
                       }else {
                           editor.putString(Constants.Category, model.getName());
                           editor.apply();

                       }/*switch (position){
                           case 0: editor.putString( Constants.Category,Constants.custom ); editor.apply();break;
                           case 1: editor.putString( Constants.Category,Constants.cars ); editor.apply();break;
                           case 2: editor.putString( Constants.Category,Constants.Celeb ); editor.apply();break;
                           case 3: editor.putString( Constants.Category,Constants.nature ); editor.apply();break;
                           case 4: editor.putString( Constants.Category,Constants.ocean ); editor.apply();break;
                           case 5: editor.putString( Constants.Category,Constants.space ); editor.apply();break;
                           case 6: editor.putString( Constants.Category,Constants.building ); editor.apply();break;
                       }*/
                       startActivity( new Intent( getApplicationContext(),SetTime.class ) );
                       startJOb();
                   } );
            }
             @Override
             public void onDataChanged() {
                 // Called each time there is a new data snapshot. You may want to use this method
                 // to hide a loading spinner or check for the "no documents" state and update your UI.
                 // ...

                 progress.dismiss();
             }

             @Override
             public void onError(@NonNull DatabaseError e) {
                 // Called when there is an error getting data. You may want to update
                 // your UI to display an error message to the user.
                 // ...
                 progress.dismiss();
                Log.e( "msg",e.getMessage());
             }
        };
         recyclerView.setAdapter(adapter);
       // recyclerView.setHasFixedSize( true );

    }
    private class ViewHolder extends RecyclerView.ViewHolder  {
        LinearLayout root;
        TextView txtTitle;
        TextView txtDesc;
        ImageView img;

        ViewHolder(View itemView) {
            super(itemView);
            root = itemView.findViewById(R.id.list_root);
            txtTitle = itemView.findViewById(R.id.list_title);
            txtDesc = itemView.findViewById(R.id.list_desc);
            img=itemView.findViewById( R.id.image );
            //Log.e( "msg","initation" );

        }

        public void setImg(String url){

            int pxw = (int) (250 * Resources.getSystem().getDisplayMetrics().density);
            int pxh = (int) (300 * Resources.getSystem().getDisplayMetrics().density);

            Picasso.get().load(url ).resize(pxw,pxh).into( img, new Callback() {
                @Override
                public void onSuccess() {

                }

                @Override
                public void onError(Exception e) {
                    e.printStackTrace();
                }
            } );

        }
        public void setTxtTitle(String string) {
            txtTitle.setText(string);
        }


        public void setTxtDesc(String string) {
            txtDesc.setText(string);
        }
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
            for (String child : children) {
                boolean success = deleteDir(new File(dir, child));
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
    protected void onStop() {
        super.onStop();
        deleteCache( getApplicationContext() );
        recyclerView.setAdapter( null );
        adapter.stopListening();
    }
    @Override
    protected void onStart() {
        super.onStart();
        sign();
        adapter.startListening();

    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
    }

    private void sign() {
        mAuth = FirebaseAuth.getInstance();
        FirebaseUser user = mAuth.getCurrentUser();
        if (user != null) {
            Log.e("TAG", "already sign");
        } else {
            signInAnonymously();
            Log.e("TAG", "sign null");
        }
        //loadIntAdd();
    }

    }
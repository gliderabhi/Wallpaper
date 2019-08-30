package com.example.abhishekhsharma.wallpaper;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.Resources;
import android.net.Uri;
import android.os.Handler;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.squareup.picasso.Picasso;

public class FileUpload extends AppCompatActivity {
    private StorageReference mStorageRef;
    Button setTime;
    Button select;
    int CODE=215;
    private SharedPreferences preferences;
    private Intent i;
    SharedPreferences.Editor editor;

    int uploadedImageNo;
    ProgressDialog progress;
    StorageReference riversRef;

    ImageView imageView;
    private DatabaseReference mDatabaseRef;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_file_upload);
        mStorageRef = FirebaseStorage.getInstance().getReference();

        select= findViewById(R.id.upload);
        select.setOnClickListener(v -> selectFile());
        imageView=findViewById(R.id.showfile);

        setTime= findViewById(R.id.select);
        setTime.setOnClickListener(v -> {
            i =new Intent(getApplicationContext(),SetTime.class);
            startActivity(i);
        });
    }
    public void selectFile ()
    {
        i = new Intent(Intent.ACTION_GET_CONTENT);
        i.setType("image/*");
        startActivityForResult(Intent.createChooser(i,"Select a file"), CODE);
    }
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if(requestCode==CODE && resultCode==RESULT_OK && data!=null) {
            String filePath = data.getDataString();

            int pxw = (int) (850 * Resources.getSystem().getDisplayMetrics().density);
            int pxh = (int) (1050 * Resources.getSystem().getDisplayMetrics().density);
            Picasso.get().load( data.getData() ).resize( pxw,pxh ).into( imageView );
            imageView.setVisibility( View.VISIBLE );
            setTime.setClickable(false);
            select.setClickable( false );
            Handler handler = new Handler();

            AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder( this );
            handler.postDelayed( () -> {
                // Actions to do after 10 seconds
                alertDialogBuilder.setMessage( "Are you sure to upload this image to your gallery? " )
                        .setPositiveButton( "Confirm ", (dialog, which) -> {

                            Uri SelectedFileLocation = Uri.parse( filePath );
                            UploadFile( SelectedFileLocation );
                        } ).setNegativeButton( "Cancel", (dialog, which) -> Toast.makeText( getApplicationContext(), "Upload cancelleed", Toast.LENGTH_LONG ).show() );
                alertDialogBuilder.create();
                alertDialogBuilder.show();
                setTime.setClickable( true );
                select.setClickable( true );
            }, 2000 );

        }
        super.onActivityResult(requestCode, resultCode, data);
    }

    public  void UploadFile(Uri file) {

        mDatabaseRef= FirebaseDatabase.getInstance().getReference();
        uploadedImageNo = 0;

        preferences = getApplicationContext().getSharedPreferences(getString(R.string.PREFS_NAME), Context.MODE_PRIVATE);
        uploadedImageNo = preferences.getInt(Constants.uploadedImageNo, 1);

        String deviceAppUID = preferences.getString(Constants.UID, "");

        riversRef = mStorageRef.child(deviceAppUID + "/" + String.valueOf(uploadedImageNo) + ".jpg");
        progress = new ProgressDialog(this);
        progress.setMessage("Uploading Image");
        progress.setProgressStyle(ProgressDialog.STYLE_SPINNER);
        progress.setIndeterminate(true);
        progress.setProgress(0);
        progress.show();

        new Thread(() -> riversRef.putFile(file)
                .addOnSuccessListener(taskSnapshot -> {
                    riversRef.getDownloadUrl().addOnSuccessListener( uri -> {
                        //Toast.makeText(getApplicationContext(),uri.toString(),Toast.LENGTH_LONG).show();

                        Toast.makeText(FileUpload.this, "Upload Success", Toast.LENGTH_SHORT).show();
                        Upload upload = new Upload(String.valueOf(uploadedImageNo) + ".jpg",
                                uri.toString());

                        DatabaseReference newRef = mDatabaseRef.child("/"+deviceAppUID).child(String.valueOf(uploadedImageNo));
                        newRef.setValue(upload);

                    } );
                    progress.dismiss();
                    progress.cancel();

                    uploadedImageNo = uploadedImageNo + 1;
                    editor = preferences.edit();
                    editor.putInt(Constants.LastImageNo,uploadedImageNo);
                    editor.putInt(Constants.uploadedImageNo, uploadedImageNo);
                    editor.apply();
                })
                .addOnFailureListener(exception -> {
                    progress.dismiss();
                    progress.cancel();
                    Toast.makeText(FileUpload.this, "Upload Failed", Toast.LENGTH_SHORT).show();
                    exception.printStackTrace();
                })).start();



    }

    @Override
    public void onDestroy() {
        super.onDestroy();


    }
    @Override
    public void onBackPressed() {
        startActivity(new Intent(getApplicationContext(),MainActivity.class));
    }
}
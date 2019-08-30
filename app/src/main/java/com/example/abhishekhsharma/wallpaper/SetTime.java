package com.example.abhishekhsharma.wallpaper;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

public class SetTime extends Activity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_set_time);
        EditText text=findViewById(R.id.TimeEntry);
        Button nbtn=findViewById(R.id.TimeSet);
        nbtn.setOnClickListener(v -> {
            String time= String.valueOf(text.getText());
            Log.e("text",time);
            SharedPreferences preferences=getApplicationContext().getSharedPreferences(Constants.PREFS_NAME, Context.MODE_PRIVATE);
            SharedPreferences.Editor editor=preferences.edit();
            editor.putString(getString(R.string.Time),time);
            editor.apply();

            Intent i =new Intent(getApplicationContext(),MainActivity.class);
            startActivity(i);
        });

    }

    @Override
    public void onBackPressed() {
        startActivity(new Intent(getApplicationContext(),MainActivity.class));
    }
}

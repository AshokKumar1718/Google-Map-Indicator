package com.example.hp.myapplication;

import android.content.Intent;
import android.service.autofill.TextValueSanitizer;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;

public class NewActivity extends AppCompatActivity {

    ListView listView;
    String[] names;
    FloatingActionButton fab ;    Fragment fragment = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_new);

       /* Intent i = getIntent();*/

        if(fragment == null){
            fragment = new MapFragment();
            FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
            ft.replace(R.id.content_frame, fragment);
            ft.commit();
            fab = findViewById(R.id.fab1);
            fab.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent i = new Intent(NewActivity.this, Report.class);
                    startActivity(i);
                }


            });
        }
    }
}
package com.example.hp.myapplication;

import android.Manifest;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Handler;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;//
import android.widget.EditText;
import android.widget.Toast;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Objects;

public class MainActivity extends AppCompatActivity {

    Button b, b2,b3;
    String s1, s2;
    String s3, s4;
    EditText name, pass ,n1,p1;
    AlertDialog.Builder builder;
    String[] permissions = new String[]{
            Manifest.permission.ACCESS_COARSE_LOCATION,
            Manifest.permission.ACCESS_FINE_LOCATION,
            Manifest.permission.ACCESS_WIFI_STATE,

    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        checkPermissions();

        s3 = "gokul";
        s1 = "1234";


        b = findViewById(R.id.button);
        b2 = findViewById(R.id.button3);

        name = findViewById(R.id.editText5);
        pass = findViewById(R.id.editText);



        b.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
//                startActivity(i);

                final Query query = FirebaseDatabase.getInstance().getReference("user" );
                query.addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        query.removeEventListener(this);
//                         if(dataSnapshot.hasChildren())
                            Log.d("wwwwww",dataSnapshot.toString());
                        for (DataSnapshot singleSnapshot : dataSnapshot.getChildren()) {

                            if (singleSnapshot.child("password").getValue().equals(pass.getText().toString())){
                                Intent i = new Intent(MainActivity.this, NewActivity.class);
                                startActivity(i);
                            }
                             else
                            {
                               // Toast.makeText(MainActivity.this, "password match" ,Toast.LENGTH_SHORT).show();
                            }


                        }
//                         else
//                         {
//                             Toast.makeText(MainActivity.this, "user not found", Toast.LENGTH_SHORT).show();
//
//                         }


                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {

                    }
                });


            }
        });





        b2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Intent i = new Intent(MainActivity.this, SignupActivity.class);
                i.putExtra("n1ame", "hello");

                //b.setVisibility(View.GONE);


//                s4 = name.getText().toString();
//                if (s1.equals(s4)) {
//
//                    Toast.makeText(MainActivity.this, " user name is matched", Toast.LENGTH_LONG).show();
//
//                } else {
//                    Toast.makeText(MainActivity.this, " user name is mis matched", Toast.LENGTH_LONG).show();
//
//                }
//
//                s2 = pass.getText().toString();
//                if (s1.equals(s2)) {
//
//                    Toast.makeText(MainActivity.this, " password is matched", Toast.LENGTH_SHORT).show();
//
//                } else
//                {
//                    Toast.makeText(MainActivity.this, " password is mis matched", Toast.LENGTH_SHORT).show();
//                }
//            if (s3.equals(s4) && s1.equals(s2))
//            {
//           startActivity(i);
//            }
//              else{
//                    Toast.makeText(MainActivity.this,"enter the correct name and password", Toast.LENGTH_LONG).show();
//            }

                startActivity(i);








            }
        });

    }

    Boolean doubleBackToExitPressedOnce = false;

    @Override
    public void onBackPressed() {
        if (doubleBackToExitPressedOnce) {
            super.onBackPressed();
            return;
        }

        this.doubleBackToExitPressedOnce = true;
        Toast.makeText(this, "Please click BACK again to exit", Toast.LENGTH_SHORT).show();

        new Handler().postDelayed(new Runnable() {

            @Override
            public void run() {
                doubleBackToExitPressedOnce = false;
            }
        }, 2000);





       /* builder = new AlertDialog.Builder(MainActivity.this);
        builder.setMessage("do u want to exit?");
        builder.setTitle("Alert");
        builder.setCancelable(false); 
        builder.setPositiveButton("YES", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                finish();
            }
        });
        builder.setNegativeButton("NO", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
            }
        });
        AlertDialog alert = builder.create();
        alert.show();*/

    }

    private boolean checkPermissions() {
        int result;
        List<String> listPermissionsNeeded = new ArrayList<>();
        for (String p : permissions) {
            result = ContextCompat.checkSelfPermission(this, p);
            if (result != PackageManager.PERMISSION_GRANTED) {
                listPermissionsNeeded.add(p);
            }
        }
        if (!listPermissionsNeeded.isEmpty()) {
            ActivityCompat.requestPermissions(this, listPermissionsNeeded.toArray(new String[listPermissionsNeeded.size()]), 100);
            return false;
        }
        return true;
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String permissions[], @NonNull int[] grantResults) {
        if (requestCode == 100) {
            if ((grantResults.length > 0)
                    && (grantResults[0] == PackageManager.PERMISSION_GRANTED)) {
                Toast.makeText(this, "gotted", Toast.LENGTH_SHORT).show();
            }

        }
    }
}
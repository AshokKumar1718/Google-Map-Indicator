package com.example.hp.myapplication;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.HashMap;

public class SignupActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_signup);
        final Intent i = getIntent();
        final EditText n1, p1;
        Button b3;
        b3 = findViewById(R.id.button6);
        n1 = findViewById(R.id.editText2);
        p1 = findViewById(R.id.editText3);
        b3.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                FirebaseDatabase database = FirebaseDatabase.getInstance();
                DatabaseReference myRef = database.getReference("user");
                HashMap<String, String> hashMap = new HashMap<>();
                hashMap.put("phone no", n1.getText().toString());
                hashMap.put("password", p1.getText().toString());
                myRef.push().setValue(hashMap);
                Log.d(".aaaaaaaaaaa", "onClick: ");
                startActivity(i);

            }
        });
    }

}

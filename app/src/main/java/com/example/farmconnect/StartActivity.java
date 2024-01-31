package com.example.farmconnect;

import android.content.Intent;
import android.os.Bundle;
import android.widget.ImageButton;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

public class StartActivity extends AppCompatActivity {

    private ImageButton next_btn;
    public TextView get_started;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_start);

        next_btn = findViewById(R.id.img_btn);
        next();



        get_started = findViewById(R.id.textViewGetStarted);
        getStarted();
    }

    public void getStarted(){
        get_started.setOnClickListener(v -> startActivity(new Intent(getApplicationContext(), UserLoginActivity.class)));
    }

    public void next(){

        next_btn.setOnClickListener(v -> {
            Intent intent = new Intent(StartActivity.this, UserLoginActivity.class);
            startActivity(intent);
        });
    }


}
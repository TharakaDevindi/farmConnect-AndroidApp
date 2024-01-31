package com.example.farmconnect;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageButton;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;

public class MessagingListActivity extends AppCompatActivity{
    private ImageButton back_btn;
    private ImageButton menu_btn;
    private ConstraintLayout constraintLayout;
    DBHelper DBHelper;

    @SuppressLint("MissingInflatedId")
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_messaging_list);

        constraintLayout = findViewById(R.id.constraintLayout);

        back_btn = findViewById(R.id.img_btn_back);
        back();
        menu_btn = findViewById(R.id.img_btn_menu);


        DBHelper = new DBHelper(this);


        constraintLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                Intent intent = new Intent(MessagingListActivity.this, ChatRoomActivity.class);
                startActivity(intent);


            }
        });



    }

    public void back(){
        back_btn.setOnClickListener(v -> new AlertDialog.Builder(this)
                .setTitle("SignOut")
                .setMessage("Are you Sure to Logout?")
                .setPositiveButton(android.R.string.yes, (dialog, which) -> {
                    Intent intent = new Intent(getApplicationContext(), UserLoginActivity.class);
                    startActivity(intent);
                    finish();
                })
                .setNegativeButton(android.R.string.no, null)
                .setIcon(android.R.drawable.ic_dialog_info)
                .show());
    }

}
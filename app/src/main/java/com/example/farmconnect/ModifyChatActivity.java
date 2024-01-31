package com.example.farmconnect;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;

import androidx.appcompat.app.AppCompatActivity;

public class ModifyChatActivity extends AppCompatActivity {

    private EditText edit_message;
    private Button edit_btn;
    private ImageButton back_btn;
    private DBHelper dbHelper;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_modify_chat);

        edit_message = findViewById(R.id.et_edit);
        edit_btn = findViewById(R.id.btn_edit);
        back_btn = findViewById(R.id.img_btn_edit_back);
        back();
    }


    public void back(){
        Intent intent = new Intent(getApplicationContext(), ChatRoomActivity.class);
        startActivity(intent);
    }
}
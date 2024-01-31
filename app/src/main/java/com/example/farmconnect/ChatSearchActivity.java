package com.example.farmconnect;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;
import java.util.List;

public class ChatSearchActivity extends AppCompatActivity {

    private EditText txt_search;
    private Button search_btn;
    private RecyclerView search_view;

    private ImageView btn_back;

    List<MessageModel> searchMessageList =  new ArrayList<>();
    DBHelper dbHelper;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat_search);

        txt_search = findViewById(R.id.et_search);
        search_btn = findViewById(R.id.btn_search);
        search();
        search_view = findViewById(R.id.search_list);

        btn_back = findViewById(R.id.img_btn_search_back);
        back();
    }


    public void search(){
        search_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

            }
        });
    }

    public void back(){
        btn_back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getApplicationContext(), ChatRoomActivity.class);
                startActivity(intent);
            }
        });
    }
}
package com.example.farmconnect;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

public class UserLoginActivity extends AppCompatActivity {

    private EditText username;
    private EditText password;

    DBHelper dbHelper;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_login);

        username = findViewById(R.id.et_username);
        password = findViewById(R.id.et_password);
        Button login_btn = findViewById(R.id.btn_login);
        TextView register = findViewById(R.id.register_text);
        dbHelper = new DBHelper(this);

        login_btn.setOnClickListener(view -> {
            String user = username.getText().toString();
            String pass = password.getText().toString();

            if (user.equals("") || pass.equals("")) {
                Toast.makeText(UserLoginActivity.this, "Please enter username and password.", Toast.LENGTH_SHORT).show();
            } else {
                Boolean checkUserPass = dbHelper.authorizeUser(user, pass);
                if (checkUserPass) {
                    Intent intent = new Intent(getApplicationContext(), ChatRoomActivity.class);
                    startActivity(intent);
                } else {
                    Toast.makeText(UserLoginActivity.this, "Invalid credentials.", Toast.LENGTH_SHORT).show();
                }
            }
        });

        register.setOnClickListener(view -> {
            Intent intent = new Intent(getApplicationContext(), UserSignUpActivity.class);
            startActivity(intent);
        });
    }
}
package com.example.farmconnect;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

public class UserSignUpActivity extends AppCompatActivity {

    private EditText username;
    private EditText password;
    private EditText confirmPassword;

    DBHelper dbHelper;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_sign_up);

        username = findViewById(R.id.et_username);
        password = findViewById(R.id.et_password);
        confirmPassword = findViewById(R.id.et_confirm_password);
        Button register = findViewById(R.id.btn_create_account);
        TextView login = findViewById(R.id.login_text);
        dbHelper = new DBHelper(this);

        register.setOnClickListener(view -> {
            String user = username.getText().toString();
            String pass = password.getText().toString();
            String confirmPass = confirmPassword.getText().toString();
            if (user.equals("") || pass.equals("") || confirmPass.equals("")){
                Toast.makeText(UserSignUpActivity.this, "Please enter values for all fields.", Toast.LENGTH_SHORT).show();
            } else {
                if (pass.equals(confirmPass)) {
                    Boolean checkUsername = dbHelper.checkUsernameExists(user);
                    if (!checkUsername) {
                        Boolean insert = dbHelper.insertData(user, pass);
                        if (insert) {
                            Intent intent = new Intent(getApplicationContext(), UserLoginActivity.class);
                            startActivity(intent);

                        } else {
                            Toast.makeText(UserSignUpActivity.this, "Registration Failed.", Toast.LENGTH_SHORT).show();
                        }
                    } else {
                        Toast.makeText(UserSignUpActivity.this, "This username is already exists. Please login.", Toast.LENGTH_SHORT).show();
                    }
                } else {
                    Toast.makeText(UserSignUpActivity.this, "Passwords are not matched. ", Toast.LENGTH_SHORT).show();
                }
            }
        });
        login.setOnClickListener(view -> {
            Intent intent = new Intent(getApplicationContext(), UserLoginActivity.class);
            startActivity(intent);
        });
    }
}
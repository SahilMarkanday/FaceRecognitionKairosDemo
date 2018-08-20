package com.facerecognitionkairosdemo.marka.facerecognitionkairosdemo;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

public class AdminLogInActivity extends AppCompatActivity {
    EditText username;
    EditText password;
    Button enter;
    String usernameInput;
    String passwordInput;
    @Override
    protected void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_adminlogin);
        username = (EditText)findViewById(R.id.username);
        password = (EditText)findViewById(R.id.password);
        enter = (Button)findViewById(R.id.enter);
        enter.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                usernameInput = username.getText().toString();
                passwordInput = password.getText().toString();
                if(usernameInput.equals("username") && passwordInput.equals("password")){
                    Intent i = new Intent(AdminLogInActivity.this, AdminActivity.class);
                    startActivity(i);
                }
                else{
                    Toast.makeText(getBaseContext(), "Incorrect Username or Password", Toast.LENGTH_LONG).show();
                }

            }
        });
    }

}

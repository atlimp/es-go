package com.example.excecutiveschedulergo;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

public class LoginActivity extends AppCompatActivity {

    Button mLoginButton;
    Button mDoneButton;
    EditText mUsernameField;
    EditText mPasswordField;
    String username, password;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        mLoginButton = findViewById(R.id.loginButton);

        mLoginButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent back = new Intent(getApplicationContext(), MainActivity.class);

                startActivity(back);
            }
        });

        // Replace default text with empty string on focus change.
        mUsernameField = findViewById(R.id.username);
        mUsernameField.setOnFocusChangeListener(new View.OnFocusChangeListener(){
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if (hasFocus && mUsernameField.getText().toString().equals("Username")) {
                    mUsernameField.setText("");
                }
            }
        });


        mPasswordField = findViewById(R.id.password);
        mPasswordField.setOnFocusChangeListener(new View.OnFocusChangeListener(){
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if (hasFocus && mPasswordField.getText().toString().equals("Password")) {
                    mPasswordField.setText("");
                }
            }
        });


        // Handle Enter key pressed inside password field.
        mPasswordField.setOnKeyListener(new View.OnKeyListener() {
            public boolean onKey(View v, int keyCode, KeyEvent event) {
                if ((event.getAction() == KeyEvent.ACTION_DOWN) &&
                        (keyCode == KeyEvent.KEYCODE_ENTER)) {
                    username = mUsernameField.getText().toString();
                    password = mPasswordField.getText().toString();
                    return true;
                }
                return false;
            }
        });

        // Handle done button press.
        mDoneButton = findViewById(R.id.done);

        mDoneButton.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v){
                username = mUsernameField.getText().toString();
                password = mPasswordField.getText().toString();
            }
        });
    }

    private void login() {













    }
}

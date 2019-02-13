package com.example.excecutiveschedulergo;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

public class LoginActivity extends AppCompatActivity {

    private Button mLoginButton;
    private Button mDoneButton;
    private EditText mUsernameField;
    private EditText mPasswordField;
    private String username, password;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        setListeners();

    }

    private void login() { }

    private void setListeners(){
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
                    sendResult();
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
                sendResult();
            }
        });
    }

    private void sendResult(){
        // From https://stackoverflow.com/questions/19666572/how-to-call-a-method-in-another-activity-from-activity
        Intent intent=new Intent();
        intent.putExtra("Username", username);
        intent.putExtra("Password", password);
        setResult(1,intent);
        finish(); //finishing activity
    }
}

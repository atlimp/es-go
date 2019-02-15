package com.example.excecutiveschedulergo;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import com.example.Connection.Connection;
import com.example.model.User;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.Response;

public class LoginActivity extends AppCompatActivity {

    private Button mLoginButton;
    private Button mDoneButton;
    private EditText mUsernameField;
    private EditText mPasswordField;
    private String username, password;
    private final Connection c = Connection.getInstance();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        setListeners();

    }

    private void login() {}

    /**
     * From https://stackoverflow.com/a/39578803
     * Sends activity result back to mainActivity.
     */
    private void sendResult(){
        Intent intent=new Intent();
        intent.putExtra("Username", username);
        intent.putExtra("Password", password);
        setResult(1,intent);
        finish(); //finishing activity
    }

    private void setUsername() {
        mUsernameField.setText(username);
    }

    /**
     * Set all listeners
     */
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
                // ...Maybe hash before sending.
                password = mPasswordField.getText().toString();

                User user = new User();

                user.setUsername(username);
                user.setPassword(password);

                c.post("/login", user, new Callback() {
                    @Override
                    public void onFailure(Call call, IOException e) {
                        Log.e("Login Activity - Login: ", e.getMessage());
                    }

                    @Override
                    public void onResponse(Call call, Response response) throws IOException {

                    }
                });

                /*

                c.get("?number=hzf64", new Callback() {
                    @Override
                    public void onFailure(Call call, IOException e) {
                        Log.e("LoginActivity: donebutton", e.getMessage());
                    }

                    @Override
                    public void onResponse(Call call, Response response) throws IOException {
                        if (response.isSuccessful()) {
                            JSONObject obj = null;
                            try {
                                obj = new JSONObject(response.body().string());

                                username = obj.toString();

                                runOnUiThread(new Runnable() {
                                    @Override
                                    public void run() {
                                        setUsername();
                                    }
                                });
                                Log.e("LoginActivity: done button", obj.toString());
                            } catch (JSONException e) {
                                e.printStackTrace();
                            }
                        }
                    }
                });

                */

                //sendResult();
            }
        });
    }
}

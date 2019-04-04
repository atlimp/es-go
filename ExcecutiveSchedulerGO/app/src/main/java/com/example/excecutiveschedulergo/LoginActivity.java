package com.example.excecutiveschedulergo;

import android.content.Intent;
import android.content.SharedPreferences;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.example.Connection.Connection;
import com.example.model.User;
import com.google.gson.Gson;

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
    private TextView mCreate;
    private TextView mTvCreate;
    private EditText mName;
    private boolean createNew;
    private String username, password, name;

    private final Connection c = Connection.getInstance();


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        createNew = false;
        setListeners();

    }

    private void finishLogin() {
        Intent intent = new Intent();
        setResult(0,intent);
        finish(); //finishing activity
    }

    private void loginFailed() {
        Log.e("Login Activity - Login: ", "Login failed");
        Toast.makeText(LoginActivity.this, R.string.loginFail, Toast.LENGTH_LONG).show();
    }

    private void registerFailed() {
        Log.e("Login Activity - Login: ", "Register failed");
        Toast.makeText(LoginActivity.this, R.string.registerFail, Toast.LENGTH_LONG).show();
    }

    private void loginSuccessful() {
        Log.e("Button pressed", "Login successful");
        Toast.makeText(LoginActivity.this, R.string.loginSuccess, Toast.LENGTH_LONG).show();
    }

    private void login(User user) {

        c.loginUser(user, new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        loginFailed();
                    }
                });
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                if (response.isSuccessful()) {
                    String res = response.body().string();

                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            loginSuccessful();
                        }
                    });

                    setToken(res);
                    finishLogin();
                } else {
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            loginFailed();
                        }
                    });
                }
            }
        });
    }

    /**
     * If create new has been pressed, this is called before
     * logging in, so the user exists when we login.
     * @param user {username,name,password}
     */
    private void create(User user){
        name = mName.getText().toString();
        user.setName(name);
        c.registerUser(user, new Callback(){
            @Override
            public void onFailure(Call call, IOException e){
                Log.e("Login Activity - Login(create): ", e.getMessage());
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        registerFailed();
                    }
                });
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                if(response.isSuccessful()) {
                    Log.e("Login Activity - Login(create): ", response.body().string());
                    login(user);
                } else {
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            registerFailed();
                        }
                    });
                }
            }
        });
    }

    /**
     * From https://stackoverflow.com/a/39578803
     * Sends activity result back to mainActivity.
     */
    private void sendResult(){

        User user = new User();

        username = mUsernameField.getText().toString();
        // ...Maybe hash before sending.
        password = mPasswordField.getText().toString();

        user.setUsername(username);
        user.setPassword(password);

        if(createNew) create(user);
        else login(user);
    }

    private void setToken(String s) {
        Gson gson = new Gson();
        User user = gson.fromJson(s, User.class);
        TokenStore.setToken(user.getToken(), this.getApplicationContext());
    }

    /**
     * Set all listeners
     */
    private void setListeners(){

        // Change name of the button
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
                sendResult();
            }
        });


        mCreate = findViewById(R.id.create);
        mName = findViewById(R.id.name);
        mTvCreate = findViewById(R.id.tvname);
        mCreate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(!createNew) {
                    mName.setVisibility(View.VISIBLE);
                    mName.setText("");
                    mTvCreate.setVisibility(View.VISIBLE);
                    mCreate.setText("Already have an account?");
                    mDoneButton.setText("Register and Login");
                    createNew = true;
                } else {
                    mCreate.setText("Click to create new User");
                    mDoneButton.setText("Login");
                    mName.setVisibility(View.GONE);
                    mTvCreate.setVisibility(View.GONE);
                    createNew = false;
                }
            }
        });

        // Handle Enter key pressed inside name field.
        mName.setOnKeyListener(new View.OnKeyListener() {
            public boolean onKey(View v, int keyCode, KeyEvent event) {
                if ((event.getAction() == KeyEvent.ACTION_DOWN) &&
                        (keyCode == KeyEvent.KEYCODE_ENTER)) {
                    sendResult();
                    return true;
                }
                return false;
            }
        });
    }
}

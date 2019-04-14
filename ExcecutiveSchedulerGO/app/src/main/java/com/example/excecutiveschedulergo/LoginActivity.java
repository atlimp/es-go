package com.example.excecutiveschedulergo;

import android.content.Intent;
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

/**
 * Used to login user by contacting backend wth information
 * input by user
 */
public class LoginActivity extends AppCompatActivity {

    private Button mDoneButton;                 // Sends information to backend
    private EditText mUsernameField;            // Holds userName string
    private EditText mPasswordField;            // Holds password string
    private TextView mCreate;                   // Shows mName and changes behavior of mDoneButton
    private TextView mTvCreate;                 // TextBox label for name if create is chosen
    private EditText mName;                     // Holds name of user
    private boolean createNew;                  // Are we creating new user
    private String username, password, name;    // Passed to backend for login
    private String registerFailMessages;        // Failure string

    // Singleton connection to backend
    private final Connection c = Connection.getInstance();


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        // Assume user exists
        createNew = false;
        setListeners();                         // Set all component listeners

    }

    /**
     * Close activity set result
     */
    private void finishLogin() {
        Intent intent = new Intent();
        setResult(0,intent);
        finish(); //finishing activity
    }

    /**
     * Shows toast with failure message for login
     */
    private void loginFailed() {
        Log.e("Login Activity - Login: ", "Login failed");
        Toast.makeText(LoginActivity.this, R.string.loginFail, Toast.LENGTH_LONG).show();
    }

    /**
     * Shows toast with failure message for registration
     */
    private void registerFailed() {
        Log.e("Login Activity - Login: ", "Register failed");
        if(registerFailMessages == null) {
            Toast.makeText(LoginActivity.this, R.string.registerFail, Toast.LENGTH_LONG).show();
        }
        else{
            Toast.makeText(LoginActivity.this, registerFailMessages, Toast.LENGTH_LONG).show();
        }
        registerFailMessages = null;
    }

    /**
     * Shows toast for successfull login
     */
    private void loginSuccessful() {
        Log.e("Button pressed", "Login successful");
        Toast.makeText(LoginActivity.this, R.string.loginSuccess, Toast.LENGTH_LONG).show();
    }

    /**
     * Logs user in
     * @param user
     */
    private void login(User user) {
        // Calls login method in connection with custom callback
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
                    // calls method on UI thread
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            loginSuccessful();
                        }
                    });
                    setToken(res);                  // Sets token in TokenStore
                    finishLogin();                  // Closes activity
                } else {
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            loginFailed();          // Shows toast
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
        // Set name field and register on backend
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
                    try {
                        JSONObject json = new JSONObject(response.body().string());
                        registerFailMessages = json.getString("message");
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }

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
     * Sets fields in User model class and creates new or logs in
     */
    private void sendResult(){
        // Set fields
        User user = new User();

        username = mUsernameField.getText().toString();
        password = mPasswordField.getText().toString();

        user.setUsername(username.toLowerCase());
        user.setPassword(password);
        // Login or create new
        if(createNew) create(user);
        else login(user);
    }

    /**
     * Set token in TokenStore
     * @param s
     */
    private void setToken(String s) {
        // Deserialize and set
        Gson gson = new Gson();
        User user = gson.fromJson(s, User.class);
        TokenStore.setToken(user.getToken(), this.getApplicationContext());
    }

    /**
     * Set all listeners
     */
    private void setListeners(){

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

    /**
     * Always go back to main
     */
    public void onBackPressed() {
        Intent main = new Intent(getApplicationContext(), MainActivity.class);
        startActivity(main);
    }
}

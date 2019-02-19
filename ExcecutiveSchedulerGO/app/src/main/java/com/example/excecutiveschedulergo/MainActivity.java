package com.example.excecutiveschedulergo;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import com.example.Connection.Connection;
import com.example.model.User;
import com.google.gson.Gson;

import java.io.IOException;
import java.util.List;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.Response;

public class MainActivity extends AppCompatActivity {

    private Button mButton;
    private TextView mHello;
    private Button mUsersButton;

    private final Connection connection = Connection.getInstance();

    String username, password;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mButton = findViewById(R.id.button);

        mButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent login = new Intent(getApplicationContext(), LoginActivity.class);
                startActivityForResult(login,1);
            }
        });

        mUsersButton = findViewById(R.id.usersButton);

        mUsersButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getUsers();
            }
        });
    }

    private void getUsers() {
        String token = TokenStore.getToken(this.getApplicationContext());
        connection.get("/users", token, new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                Log.e("Get users", e.getMessage());
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                if (response.isSuccessful()) {
                    String res = response.body().string();
                    Log.e("Got response", res);
                } else {
                    Log.e("No response", "Bla");
                }
            }
        });
    }

    @Override
    protected void onStop(){
        super.onStop();
        Log.e("Main activity on stop: ", "stopped");
    }
}

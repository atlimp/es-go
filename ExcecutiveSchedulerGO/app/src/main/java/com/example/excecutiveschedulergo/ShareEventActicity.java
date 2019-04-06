package com.example.excecutiveschedulergo;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import com.example.Connection.Connection;
import com.example.model.Event;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.Response;

public class ShareEventActicity extends AppCompatActivity {

    private EditText mUsername;
    private Button mButton;

    private Toolbar toolbar;
    private String users;
    private Connection c = Connection.getInstance();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_share_event_acticity);

        toolbar = new Toolbar(this);
        getUsers();
        setListeners();
    }

    private void setListeners() {
        mUsername = findViewById(R.id.share_event_text);

        mUsername.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if (hasFocus && mUsername.getText().toString().equals("Username")) {
                    mUsername.setText("");
                }
            }
        });

        mButton = findViewById(R.id.share_event_button);

        mButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                shareEvent();
            }
        });
    }

    private void getUsers(){
        String token = TokenStore.getToken(this.getApplicationContext());
        c.getAllUsers(token, new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                Log.e("Get Users", "Failed to get users");
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                if (response.isSuccessful()) {
                    users = response.body().toString();
                    Log.v("Share event success", users);
                } else {
                    Log.e("Share event fail", users);
                }
            }

        });
    }

    private void shareEvent() {
        String token = TokenStore.getToken(this.getApplicationContext());
        Event event = (Event) (getIntent()).getParcelableExtra("Event");
        List<String> usernames = new ArrayList<String>();

        usernames.add(mUsername.getText().toString());

        c.shareEvent(usernames, event, token, new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                Log.e("Share Event", "Failed to share event");
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                String json = response.body().string();

                if (response.isSuccessful()) {
                    Log.v("Share event success", json);
                    // Redirectum aftur i calendar view eftir ad vid shareum event
                    Intent calendar = new Intent(getApplicationContext(), CalendarActivity.class);
                    startActivity(calendar);

                } else {
                    Log.e("Share event fail", json);
                }
            }
        });
    }
}

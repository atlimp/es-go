package com.example.excecutiveschedulergo;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import com.example.Connection.Connection;
import java.io.IOException;
import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.Response;

public class MainActivity extends AppCompatActivity {

    private Button mButton;
    private Button mUsersButton;

    private final Connection c = Connection.getInstance();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Intent create = new Intent(getApplicationContext(), CreateEventActivity.class);
        startActivity(create);

        mButton = findViewById(R.id.button);

        mButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent login = new Intent(getApplicationContext(), LoginActivity.class);
                startActivityForResult(login,1);
            }
        });

        mUsersButton = findViewById(R.id.usersButton);
    }

    @Override
    protected void onStop(){
        super.onStop();
        Log.e("Main activity on stop: ", "stopped");
    }
}

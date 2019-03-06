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

    private Button mLoginButton;
    private Button mCalendarButton;
    private Button mCreateEventButton;

    private final Connection c = Connection.getInstance();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mLoginButton = findViewById(R.id.login_button);
        mCalendarButton = findViewById(R.id.calendar_button);
        mCreateEventButton = findViewById(R.id.create_event_button);

        mLoginButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent login = new Intent(getApplicationContext(), LoginActivity.class);
                startActivity(login);
            }
        });

        mCalendarButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent calendar = new Intent(getApplicationContext(), CalendarActivity.class);
                startActivity(calendar);
            }
        });

        mCreateEventButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent createEvent = new Intent(getApplicationContext(), CreateEventActivity.class);
                Bundle bundle = new Bundle();
                bundle.putInt("Type", 0);
                createEvent.putExtras(bundle);
                startActivity(createEvent);
            }
        });
    }

    @Override
    protected void onStop(){
        super.onStop();
        Log.e("Main activity on stop: ", "stopped");
    }
}

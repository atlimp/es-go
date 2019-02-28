package com.example.excecutiveschedulergo;

import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;

import com.example.Connection.Connection;
import com.example.model.Event;

import java.io.IOException;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.Response;

public class CreateEventActivity extends AppCompatActivity {

    private Button mButton;

    private EditText mTitle;
    private EditText mDescription;
    private DatePicker mStartDate;
    private DatePicker mEndDate;

    private final Connection c = Connection.getInstance();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_event);

        mTitle = findViewById(R.id.create_title);
        mDescription = findViewById(R.id.create_description);
        mStartDate = findViewById(R.id.create_startDate);
        mEndDate = findViewById(R.id.create_endDate);

        mButton = findViewById(R.id.create_button);

        mButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                createEvent();
            }
        });
    }

    private void createEvent() {
        String title = mTitle.getText().toString();
        String description = mDescription.getText().toString();
        Calendar start = new GregorianCalendar(
                mStartDate.getYear(),
                mStartDate.getMonth(),
                mStartDate.getDayOfMonth()
        );

        Calendar end = new GregorianCalendar(
                mEndDate.getYear(),
                mEndDate.getMonth(),
                mEndDate.getDayOfMonth()
        );

        Date startDate = new Date(start.getTimeInMillis());
        Date endDate = new Date(end.getTimeInMillis());

        Event event = new Event();

        event.setTitle(title);
        event.setDescription(description);
        event.setStartDate(startDate);
        event.setEndDate(endDate);


        String token = TokenStore.getToken(this.getApplicationContext());
        c.createEvent(event, token, new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                Log.e("Create Event: ", e.getMessage());
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                String json = response.body().string();

                if (response.isSuccessful()) {
                    Log.e("Create event", json);
                } else {
                    Log.e("Create event", json);
                }
            }
        });
    }

}

package com.example.excecutiveschedulergo;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TimePicker;

import com.example.Connection.Connection;
import com.example.model.Event;

import java.io.IOException;
import java.util.Calendar;
import java.util.Date;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.Response;

public class CreateEventActivity extends AppCompatActivity {

    private Button mButton;

    private EditText mTitle;
    private EditText mDescription;
    private Button mStartDateText;
    private Button mEndDateText;
    private DatePicker mStartDate;
    private TimePicker mTimePicker;
    private Button mSetDateButton;
    private Button mSetTimeButton;
    private DatePicker mEndDate;
    private LinearLayout mUpperInfo;
    private Toolbar toolbar;

    private Boolean pickStart;

    private long pickedStart;
    private long pickedEnd;

    private int year, month, day, hour, minute;

    private final Connection c = Connection.getInstance();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_event);

        toolbar = new Toolbar(this);
        setListeners();

        Bundle bundle = getIntent().getExtras();
        setPurpose(bundle);
    }


    private void setListeners() {

        mUpperInfo = findViewById(R.id.upper_info);
        mTitle = findViewById(R.id.create_title);
        mDescription = findViewById(R.id.create_description);

        mStartDateText = findViewById(R.id.startDateTextBox);
        mEndDateText = findViewById(R.id.endDateTextBox);

        mStartDate = findViewById(R.id.create_startDate);

        // Set variables with current date.
        Calendar calendar = Calendar.getInstance();
        calendar.setTimeInMillis(System.currentTimeMillis());
        year = calendar.get(Calendar.YEAR);
        month = calendar.get(Calendar.MONTH);
        day = calendar.get(Calendar.DAY_OF_MONTH);

        mStartDateText.setText(calendar.getTime().toString());
        mEndDateText.setText(calendar.getTime().toString());

        pickedStart = calendar.getTimeInMillis();
        pickedEnd = calendar.getTimeInMillis();

        mTitle.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if (hasFocus && mTitle.getText().toString().equals("Title")) {
                    mTitle.setText("");
                }
            }
        });

        mDescription.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if (hasFocus && mDescription.getText().toString().equals("Description")) {
                    mDescription.setText("");
                }
            }
        });

        // From https://stackoverflow.com/a/34952495
        // Update variables with user input.
        mStartDate.init(year, month, day, new DatePicker.OnDateChangedListener() {
            @Override
            public void onDateChanged(DatePicker mStartDate, int _year, int _month, int _day) {
                year = _year;
                month = _month;
                day = _day;
                calendar.set(year, month, day);
                Log.v("CreateEventActivity.onDateChanged: ", pickStart.toString());
                if (pickStart) {
                    mStartDateText.setText(calendar.getTime().toString());
                    pickedStart = calendar.getTimeInMillis();
                    // Always keep end of event at least equal to start
                    if (pickedEnd < pickedStart) {
                        pickedEnd = pickedStart;
                        mEndDateText.setText(calendar.getTime().toString());
                    }
                } else {
                    mEndDateText.setText(calendar.getTime().toString());
                    pickedEnd = calendar.getTimeInMillis();
                    // Always keep end of event at least equal to start
                    if (pickedEnd < pickedStart) {
                        pickedStart = pickedEnd;
                        mStartDateText.setText(calendar.getTime().toString());
                    }
                }
            }
        });

        mTimePicker = findViewById(R.id.time_picker);

        mTimePicker.setOnTimeChangedListener(new TimePicker.OnTimeChangedListener() {
            @Override
            public void onTimeChanged(TimePicker view, int _hour, int _minute) {
                hour = _hour;
                minute = _minute;
                calendar.set(year, month, day, hour, minute);
                if (pickStart) {
                    mStartDateText.setText(calendar.getTime().toString());
                    pickedStart = calendar.getTimeInMillis();
                } else {
                    mEndDateText.setText(calendar.getTime().toString());
                    pickedEnd = calendar.getTimeInMillis();
                }
            }
        });

        mSetDateButton = findViewById(R.id.set_date_button);
        mSetDateButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mStartDate.setVisibility(View.GONE);
                mSetDateButton.setVisibility(View.GONE);
                mTimePicker.setVisibility(View.VISIBLE);
                mSetTimeButton.setVisibility(View.VISIBLE);
            }
        });

        mSetTimeButton = findViewById(R.id.set_time_button);
        mSetTimeButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mTimePicker.setVisibility(View.GONE);
                mSetTimeButton.setVisibility(View.GONE);
                if (pickStart) pickedStart = calendar.getTimeInMillis();
                else pickedEnd = calendar.getTimeInMillis();

                mUpperInfo.setVisibility(View.VISIBLE);
            }
        });


        //mEndDate = findViewById(R.id.create_endDate);

        mButton = findViewById(R.id.create_button);

        mButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                createEvent();
            }
        });

        // Click listeners for date text boxes (Buttons).
        mStartDateText.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                pickStart = true;
                mStartDate.setVisibility(View.VISIBLE);
                mSetDateButton.setVisibility(View.VISIBLE);
                mUpperInfo.setVisibility(View.GONE);
            }
        });

        mEndDateText.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                pickStart = false;
                Log.v("CreateEventActivity.mEndDateText.onClick: ", pickStart.toString());
                mStartDate.setVisibility(View.VISIBLE);
                mSetDateButton.setVisibility(View.VISIBLE);
                //mEndDate.setMinDate(pickedStart);
                mUpperInfo.setVisibility(View.GONE);
            }
        });
    }

    private void createEvent() {
        String title = mTitle.getText().toString();
        String description = mDescription.getText().toString();

        /**
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
         */
        Date startDate = new Date(pickedStart);
        Date endDate = new Date(pickedEnd);

        Event event = new Event();

        event.setTitle(title);
        event.setDescription(description);
        event.setStartDate(startDate);
        event.setEndDate(endDate);


        String token = TokenStore.getToken(this.getApplicationContext());
        Log.e("Token: ", token);
        c.createEvent(event, token, new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                Log.e("Create Event: ", e.getMessage());
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                String json = response.body().string();

                if (response.isSuccessful()) {
                    Log.v("Create event", json);
                } else {
                    Log.e("Create event", json);
                }
            }
        });
    }

    /**
     * Sets Activity layout according to int passed when the activity
     * was created. The int is passed with bundle.
     * From https://stackoverflow.com/a/3913720
     * @param bundle
     */
    private void setPurpose(Bundle bundle){
        int num = -1;
        if(bundle != null){
            num = bundle.getInt("Type");
        }
        switch(num){

            case 0:
                //do something
                break;

            case 1:
                //do something
                break;

            case 2:
                //do something
                break;

            case -1:
                finish();
                break;
        }

    }

}

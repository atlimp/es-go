package com.example.excecutiveschedulergo;

import android.content.Intent;
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
import java.util.Locale;
import java.util.Set;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.Response;

public class CreateEventActivity extends AppCompatActivity {

    private Button mButton;
    private Button mCancel;
    private Button mDelete;

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

    private Event event;

    private int year, month, day, hour, minute;
    private int purpose;

    private final Connection c = Connection.getInstance();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_event);

        toolbar = new Toolbar(this);
        setListeners();
        purpose = setPurpose(getIntent());
        mTimePicker.setIs24HourView(true);
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

        mButton = findViewById(R.id.confirm_button);
        mCancel = findViewById(R.id.cancel_button);
        mDelete  = findViewById(R.id.delete_button);

        mCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.v("Cancel","");
                Intent calendar = new Intent(getApplicationContext(), FragmentActivity.class);
                startActivity(calendar);
            }
        });

        mButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.v("CreateEventListener","Purpose: " + purpose);
                switch(purpose){
                    case 0:
                        createEvent();
                        break;
                    case 1:
                        editEvent();
                        break;
                    case 2:
                        shareEvent();
                        finish();
                        break;
                    case 3:
                        finish();
                        break;
                }
            }
        });

        mDelete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.v("Delete Event","");
                deleteEvent();
            }
        });

        // Click listeners for date text boxes (Buttons).
        mStartDateText.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mTimePicker.setVisibility(View.GONE);
                mSetTimeButton.setVisibility(View.GONE);
                pickedStart = calendar.getTimeInMillis();
                pickStart = true;
                mStartDate.setVisibility(View.VISIBLE);
                mSetDateButton.setVisibility(View.VISIBLE);
                mUpperInfo.setVisibility(View.GONE);
            }
        });

        mEndDateText.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mTimePicker.setVisibility(View.GONE);
                mSetTimeButton.setVisibility(View.GONE);
                pickedEnd = pickedStart;
                pickStart = false;
                Log.v("CreateEventActivity.mEndDateText.onClick: ", pickStart.toString());
                mStartDate.setVisibility(View.VISIBLE);
                mSetDateButton.setVisibility(View.VISIBLE);
                mUpperInfo.setVisibility(View.GONE);
            }
        });
    }

    private void createEvent() {
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

        event = new Event();
        setFields();

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
                    // Þegar búið er að gera event er notandanum síðan redirectað aftur í calendar.
                    Intent calendar = new Intent(getApplicationContext(), FragmentActivity.class);
                    startActivity(calendar);
                } else {
                    Log.e("Create event", json);
                }
            }
        });
    }

    /**
     * Finds event in backend and saves the new data to it.
     * @param
     */
    private void editEvent(){
        //TODO: unite code between methods.

        String token = TokenStore.getToken(this.getApplicationContext());
        setFields();

        c.editEvent(event,token, new Callback(){
            @Override
            public void onFailure(Call call, IOException e) {
                Log.e("Edit Event: ", e.getMessage());
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                String json = response.body().string();

                if (response.isSuccessful()) {
                    Log.v("Edit event", json);
                    // Þegar notandi hefur klárað að edita event er honum redirectað í calendar
                    Intent calendar = new Intent(getApplicationContext(), FragmentActivity.class);
                    startActivity(calendar);
                } else {
                    Log.e("Edit event", json);
                }
            }
        });


    }

    public void deleteEvent(){
        //TODO: unite code between methods.

        String token = TokenStore.getToken(this.getApplicationContext());

        c.deleteEvent(event,token, new Callback(){
            @Override
            public void onFailure(Call call, IOException e) {
                Log.e("Delete Event: ", e.getMessage());
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                String json = response.body().string();

                if (response.isSuccessful()) {
                    Log.e("Delete Event", json);
                    // Þegar notandi hefur klárað að deleta event er honum redirectað í calendar
                    Intent calendar = new Intent(getApplicationContext(), FragmentActivity.class);
                    startActivity(calendar);
                } else {
                    Log.e("Delete Event", json);
                }
            }
        });


    }

    private void shareEvent(){
        //TODO: Hide elements and replace with user list, add user button and textfield.
        editEvent();
    }

    /**
     * Sets Activity layout according to int passed when the activity
     * was created. The int is passed with bundle.
     * From https://stackoverflow.com/a/3913720
     * @param intent
     */
    private int setPurpose(Intent intent){
        int num = intent.getIntExtra("Type",-1);
        switch(num){
            case 0:
                //create
                mDelete.setVisibility(View.GONE);
                break;
            case 1:
                //edit
                event = (Event) intent.getParcelableExtra("Event");
                mButton.setText("Edit");
                mTitle.setText(event.getTitle());
                mDescription.setText(event.getDescription());

                Date sd = event.getStartDate();
                mStartDateText.setText(sd.toString());
                Date ed = event.getEndDate();
                mEndDateText.setText(ed.toString());

                pickedStart = sd.getTime();
                pickedEnd = ed.getTime();
                break;
            case 2:
                //share
                event = (Event) intent.getParcelableExtra("Event");
                mButton.setText("Share");
                break;
            case -1:
                // Shouldn't happen.
                finish();
                break;
        }
        return num;
    }

    private void setFields(){
        String title = mTitle.getText().toString();
        if(title == null || title.equals("")) title = "Title";
        event.setTitle(title);
        event.setDescription(mDescription.getText().toString());
        event.setStartDate(new Date(pickedStart));
        event.setEndDate(new Date(pickedEnd));
    }
}

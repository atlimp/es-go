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
import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.Response;

/**
 * This activity serves one of three possible purposes.
 * It can create, edit or share an event.
 * The activity is called with an int in the extras that will specify
 * its purpose. In the event of edit or share the extras will also contain
 * a parcelable Event object.
 */
public class CreateEventActivity extends AppCompatActivity {

    private Button mButton;             // Modifies event
    private Button mCancel;             // Cancels modification
    private Button mDelete;             // Deletes current event
    private EditText mTitle;            // Title textbox for edit or create
    private EditText mDescription;      // Description textbox for edit or create
    private LinearLayout mUpperInfo;    // Upper textboxes
    private Button mStartDateText;      // Button that pops up datePicker
    private Button mEndDateText;        // also contains current dateTime of event
    private DatePicker mStartDate;      // DatePicker for both start and end dates
    private TimePicker mTimePicker;     // TimePicker for both start and end
    private Button mSetDateButton;      // Sets date and pops up timePicker
    private Button mSetTimeButton;      // Sets time and hides timePicker
    private Toolbar toolbar;            // create edit share and logout buttons

    private Boolean pickStart;          // Are we picking start or end date
    private long pickedStart;           // Start date that has been piked
    private long pickedEnd;             // End date that has been picked

    private Event event;                // Event that is being created or modified

    // Parts of date for calculations
    private int year, month, day, hour, minute;
    private int purpose;                // Purpose included in extras

    // Singleton connection to database
    private final Connection c = Connection.getInstance();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_event);

        toolbar = new Toolbar(this);    // Toolbar created for this activity
        setListeners();                         // All component listeners set
        purpose = setPurpose(getIntent());      // Purpose set
        mTimePicker.setIs24HourView(true);      // TimePicker appearance
    }

    /**
     * Sets all xml components and component listeners
     */
    private void setListeners() {

        mUpperInfo = findViewById(R.id.upper_info);             // Layout holding 2 textBoxes
        mTitle = findViewById(R.id.create_title);               // Title textBox inside mUpperInfo
        mDescription = findViewById(R.id.create_description);   // Description tb inside mUpperInfo

        // Date buttons
        mStartDateText = findViewById(R.id.startDateTextBox);
        mEndDateText = findViewById(R.id.endDateTextBox);

        // DatePicker
        mStartDate = findViewById(R.id.create_startDate);

        // Set variables with current date.
        Calendar calendar = Calendar.getInstance();
        calendar.setTimeInMillis(System.currentTimeMillis());
        year = calendar.get(Calendar.YEAR);
        month = calendar.get(Calendar.MONTH);
        day = calendar.get(Calendar.DAY_OF_MONTH);

        mStartDateText.setText(calendar.getTime().toString());
        mEndDateText.setText(calendar.getTime().toString());

        // Current event start and end
        pickedStart = calendar.getTimeInMillis();
        pickedEnd = calendar.getTimeInMillis();

        // When textBox is chosen, remove string so user can input hs own
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

        // Control component visibility when picking dates
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

        // Control visibility and set dateTime
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

        mButton = findViewById(R.id.confirm_button);
        mCancel = findViewById(R.id.cancel_button);
        mDelete  = findViewById(R.id.delete_button);

        // Cancels event modification and calls FragmentActivity
        mCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.v("Cancel","");
                Intent calendar = new Intent(getApplicationContext(), FragmentActivity.class);
                startActivity(calendar);
            }
        });

        // Modifies event according to purpose variable from extras
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

        // Deletes event
        mDelete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.v("Delete Event","");
                deleteEvent();
            }
        });

        // Click listeners for date text boxes (Buttons).
        // Controls visibility
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

    /**
     * Creates a new event and updates backend
     */
    private void createEvent() {
        event = new Event();
        setFields();

        // Backend authentication
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
                    // When event has been created user is redirected back to FragmentActivity
                    Intent calendar = new Intent(getApplicationContext(), FragmentActivity.class);
                    startActivity(calendar);
                } else {
                    // If unauthorized redirect to login.
                    int statusCode = response.code();
                    if (statusCode == 401) {
                        TokenStore.deleteToken(getApplicationContext());
                        Intent login = new Intent(getApplicationContext(), LoginActivity.class);
                        startActivity(login);
                    }
                    Log.e("Create event", json);
                }
            }
        });
    }

    /**
     * Finds event in backend and saves the new data to it.
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
                    // When event has been edited user is redirected back to FragmentActivity
                    Intent calendar = new Intent(getApplicationContext(), FragmentActivity.class);
                    startActivity(calendar);
                } else {
                    // If unauthorized redirect to login.
                    int statusCode = response.code();
                    if (statusCode == 401) {
                        TokenStore.deleteToken(getApplicationContext());
                        Intent login = new Intent(getApplicationContext(), LoginActivity.class);
                        startActivity(login);
                    }
                    Log.e("Edit event", json);
                }
            }
        });


    }

    /**
     * Deletes event and updates backend
     */
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
                    // When event has been deleted user is redirected back to FragmentActivity
                    Intent calendar = new Intent(getApplicationContext(), FragmentActivity.class);
                    startActivity(calendar);
                } else {
                    // If unauthorized redirect to login.
                    int statusCode = response.code();
                    if (statusCode == 401) {
                        TokenStore.deleteToken(getApplicationContext());
                        Intent login = new Intent(getApplicationContext(), LoginActivity.class);
                        startActivity(login);
                    }
                    Log.e("Delete Event", json);
                }
            }
        });


    }

    private void shareEvent(){
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

    /**
     * Sets current events fields
     */
    private void setFields(){
        // Make sure event starts before it ends
        if(pickedEnd < pickedStart) {
            long temp = pickedStart;
            pickedStart = pickedEnd;
            pickedEnd = temp;
        }
        String title = mTitle.getText().toString();
        if(title == null || title.equals("")) title = "Title";
        event.setTitle(title);
        event.setDescription(mDescription.getText().toString());
        event.setStartDate(new Date(pickedStart));
        event.setEndDate(new Date(pickedEnd));
    }

    /**
     * Go back to FragmentActivity, not last Activity.
     */
    public void onBackPressed() {
        Intent calendar = new Intent(getApplicationContext(), FragmentActivity.class);
        startActivity(calendar);
    }


}

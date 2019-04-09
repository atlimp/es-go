package com.example.excecutiveschedulergo;

import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.ScrollView;
import android.widget.TextView;

import com.example.Connection.Connection;
import com.example.model.Event;
import com.example.model.User;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.io.IOException;
import java.lang.reflect.Type;
import java.text.DateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.List;
import java.util.Locale;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.Response;



public class LandscapeFragment extends Fragment {

    FragmentActivity activity;

    RelativeLayout[] days;
    ScrollView mLandscapeScrollview;

    Button mLandscapePrevButton;
    Button mLandscapeNextButton;
    TextView mLandscapeCurrentWeek;

    Connection c = Connection.getInstance();

    Calendar startCal;
    Calendar endCal;

    View view;

    LinearLayout mCardView;

    List<Event> events;
    List<View> eventViews;

    public void setActivity(FragmentActivity activity) {
        this.activity = activity;
    }

    public LandscapeFragment() {
        // Required empty public constructor
    }

    private void initDays(View view) {
        RelativeLayout mCalendarMonday = view.findViewById(R.id.calendarMonday);
        RelativeLayout mCalendarTuesday = view.findViewById(R.id.calendarTuesday);
        RelativeLayout mCalendarWednesday = view.findViewById(R.id.calendarWednesday);
        RelativeLayout mCalendarThursday = view.findViewById(R.id.calendarThursday);
        RelativeLayout mCalendarFriday = view.findViewById(R.id.calendarFriday);
        RelativeLayout mCalendarSaturday = view.findViewById(R.id.calendarSaturday);
        RelativeLayout mCalendarSunday = view.findViewById(R.id.calendarSunday);

        days = new RelativeLayout[]{
                mCalendarMonday,
                mCalendarTuesday,
                mCalendarWednesday,
                mCalendarThursday,
                mCalendarFriday,
                mCalendarSaturday,
                mCalendarSunday
        };
    }

    private void setCurrWeek() {
        Locale loc = new Locale("is", "IS");
        DateFormat df = DateFormat.getDateInstance(DateFormat.SHORT, loc);

        String dateString = df.format(new Date(startCal.getTimeInMillis())) + " - " + df.format(new Date(endCal.getTimeInMillis()));

        mLandscapeCurrentWeek.setText(dateString);
    }

    private String getTimeString(Date d1, Date d2) {
        Locale loc = new Locale("is", "IS");
        DateFormat df = DateFormat.getTimeInstance(DateFormat.SHORT, loc);

        return df.format(d1) + " - " + df.format(d2);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.fragment_landscape, container, false);

        initDays(view);

        mLandscapeScrollview = view.findViewById(R.id.landscape_scrollview);
        mCardView = view.findViewById(R.id.CardView);
        mLandscapeNextButton = view.findViewById(R.id.landscape_next_button);
        mLandscapePrevButton = view.findViewById(R.id.landscape_prev_button);
        mLandscapeCurrentWeek = view.findViewById(R.id.landscape_current_week);

        eventViews = new ArrayList<View>();

        setListeners();
        setDate();
        reloadData();

        // Hide title
        activity.getSupportActionBar().hide();

        return view;
    }

    private void setDate() {
        Calendar today = new GregorianCalendar();

        startCal = new GregorianCalendar(today.get(Calendar.YEAR), today.get(Calendar.MONTH), today.get(Calendar.DAY_OF_MONTH));
        int dayOfWeek = (startCal.get(Calendar.DAY_OF_WEEK) + 5) % 7;
        startCal.add(Calendar.DAY_OF_YEAR, dayOfWeek);

        endCal = (Calendar) startCal.clone();
        endCal.add(Calendar.DAY_OF_YEAR, 6);
        endCal.add(Calendar.HOUR_OF_DAY, 23);
        endCal.add(Calendar.MINUTE, 59);
        endCal.add(Calendar.SECOND, 59);
    }

    private void setListeners() {
        mCardView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mCardView.setVisibility(View.GONE);
            }
        });


        mLandscapePrevButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startCal.add(Calendar.DATE, -7);
                endCal.add(Calendar.DATE, -7);
                reloadData();
            }
        });

        mLandscapeNextButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startCal.add(Calendar.DATE, 7);
                endCal.add(Calendar.DATE, 7);
                reloadData();
            }
        });
    }

    private String dateString(Date date) {
        Locale loc = new Locale("is", "IS");
        DateFormat dateFormat = DateFormat.getDateInstance(DateFormat.DEFAULT, loc);
        DateFormat timeFormat = DateFormat.getTimeInstance(DateFormat.DEFAULT, loc);

        return dateFormat.format(date) + " " + timeFormat.format(date);
    }

    private void unselectAll(View view) {
        for (View v : eventViews) {
            if (v != view)
                v.setSelected(false);
        }
    }

    private void setEvents(List<Event> events) {
        this.events = events;
    }

    private void updateUI() {

        // Scroll to correct hour
        int hour = new GregorianCalendar().get(Calendar.HOUR_OF_DAY);

        int yOff = (int) ((hour - 2) * 30 * getResources().getDisplayMetrics().density);
        mLandscapeScrollview.smoothScrollTo(0, yOff);

        for (Event e : events) {
            LayoutInflater inflater = (LayoutInflater) activity.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            View view = inflater.inflate(R.layout.event_layout, null);
            TextView title = view.findViewById(R.id.event_landscape_title);
            TextView time = view.findViewById(R.id.event_landscape_time);

            title.setText(e.getTitle());
            time.setText(getTimeString(e.getStartDate(), e.getEndDate()));


            RelativeLayout.LayoutParams params = new RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.MATCH_PARENT, RelativeLayout.LayoutParams.WRAP_CONTENT);
            float density = getResources().getDisplayMetrics().density;

            Calendar start = new GregorianCalendar();
            start.setTime(e.getStartDate());

            Calendar end = new GregorianCalendar();
            end.setTime(e.getEndDate());

            // Number of half hour timeslots
            int startTime = (start.get(Calendar.HOUR_OF_DAY) * 60) + start.get(Calendar.MINUTE);
            // Offset from top in dp
            int topOff = (int) ((1.0 * startTime / 60) * 30 * density);

            long duration = (end.getTimeInMillis() - start.getTimeInMillis()) / (1000 * 60);
            // Height in dp
            int height = (int) ((1.0 * duration / 60) * 30 * density);

            int minHeight = (int) (30 * density);

            height = height < minHeight ? minHeight : height;

            // Week starts at monday
            int index = (start.get(Calendar.DAY_OF_WEEK) + 5) % 7;

            params.setMargins(0, topOff, 0, 0);
            params.height = height;

            view.setLayoutParams(params);

            view.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Log.e("Clicked event", "ID: " + e.getId() + " title: " + e.getTitle() + e.toString());
                    activity.toolbar.event = e;

                    unselectAll(view);

                    if (view.isSelected())
                        view.setSelected(false);
                    else
                        view.setSelected(true);
                }
            });

            view.setOnLongClickListener(new View.OnLongClickListener() {
                @Override
                public boolean onLongClick(View v) {
                    View whole = LandscapeFragment.this.view;

                    mCardView.setVisibility(View.VISIBLE);
                    TextView mTitle = whole.findViewById(R.id.CardTitle);
                    mTitle.setText(e.getTitle());
                    TextView mDescription = whole.findViewById(R.id.CardDescription);
                    mDescription.setText(e.getDescription());
                    TextView mStartDate = whole.findViewById(R.id.CardStartDate);
                    mStartDate.setText(dateString(e.getStartDate()));
                    TextView mEndDate = whole.findViewById(R.id.CardEndDate);
                    mEndDate.setText(dateString(e.getEndDate()));

                    // Set userlist on card
                    ListView mUserList = whole.findViewById(R.id.CardUsers);
                    List<User> users = e.getUsers();

                    ArrayAdapter<User> adapter = new ArrayAdapter<User>(
                            activity.getApplicationContext(),
                            android.R.layout.simple_list_item_1,
                            users
                    );

                    mUserList.setAdapter(adapter);
                    return true;
                }
            });

            eventViews.add(view);

            // Add event to correct day
            days[index].addView(view);
        }
    }

    private void reloadData() {
        setCurrWeek();

        eventViews.clear();

        for (int i = 0; i < days.length; i++) {
            days[i].removeAllViews();
        }

        String token = TokenStore.getToken(activity.getApplicationContext());
        c.getEvents(new Date(startCal.getTimeInMillis()), new Date(endCal.getTimeInMillis()), token, new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                Log.e("Get events", e.getMessage());
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                String json = response.body().string();

                if (response.isSuccessful()) {
                    Log.e("Get events", json);
                    Gson gson = new Gson();

                    Type type = new TypeToken<ArrayList<Event>>(){}.getType();
                    List events = gson.fromJson(json, type);
                    Log.e("List size", "" + events.size());

                    setEvents(events);

                    activity.runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            updateUI();
                        }
                    });
                } else {
                    // If unauthorized redirect to login.
                    int statusCode = response.code();
                    if (statusCode == 401) {
                        activity.redirectToLogin();
                    }
                    Log.e("Get events", json);
                }
            }
        });

    }
}

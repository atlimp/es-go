package com.example.excecutiveschedulergo;

import android.content.Context;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.ProgressBar;
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


public class PortraitFragment extends Fragment {

    ListView mList;
    Toolbar  toolbar;
    Connection c = Connection.getInstance();

    FragmentActivity activity;
    View view;

    Button mPrevButton;
    Button mNextButton;
    TextView mCurrentDate;
    ProgressBar mCalendarProgress;

    Calendar startCal;
    Calendar endCal;

    public PortraitFragment() {
        // Required empty public constructor
    }

    public void setActivity(FragmentActivity activity) {
        this.activity = activity;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        view = inflater.inflate(R.layout.fragment_portrait, container, false);

        mList = view.findViewById(R.id.list);
        toolbar = new Toolbar(activity);

        mPrevButton = view.findViewById(R.id.prevDayButton);
        mNextButton = view.findViewById(R.id.nextDayButton);

        mCurrentDate = view.findViewById(R.id.currentDate);

        mCalendarProgress = view.findViewById(R.id.calendarProgress);

        Calendar today = new GregorianCalendar();

        startCal = new GregorianCalendar(today.get(Calendar.YEAR), today.get(Calendar.MONTH), today.get(Calendar.DAY_OF_MONTH));
        endCal = (Calendar) startCal.clone();
        endCal.add(Calendar.DATE, 1);

        setListeners();

        reloadData();

        return view;
    }

    private String dateString(Date date) {
        Locale loc = new Locale("is", "IS");
        DateFormat dateFormat = DateFormat.getDateInstance(DateFormat.DEFAULT, loc);
        DateFormat timeFormat = DateFormat.getTimeInstance(DateFormat.DEFAULT, loc);

        return dateFormat.format(date) + " " + timeFormat.format(date);
    }

    private void setListeners(){
        mList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                toolbar.event = (Event) parent.getItemAtPosition(position);

            }
        });

        mList.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener(){
            @Override
            public boolean onItemLongClick(AdapterView<?> parent, View view, int position, long id){
                Event event = (Event) parent.getItemAtPosition(position);
                LinearLayout mCardView = view.findViewById(R.id.CardView);
                mCardView.setVisibility(View.VISIBLE);
                TextView mTitle = view.findViewById(R.id.CardTitle);
                mTitle.setText(event.getTitle());
                TextView mDescription = view.findViewById(R.id.CardDescription);
                mDescription.setText(event.getDescription());
                TextView mStartDate = view.findViewById(R.id.CardStartDate);
                mStartDate.setText(dateString(event.getStartDate()));
                TextView mEndDate = view.findViewById(R.id.CardEndDate);
                mEndDate.setText(dateString(event.getEndDate()));

                // Set userlist on card
                ListView mUserList = view.findViewById(R.id.CardUsers);
                List<User> users = event.getUsers();

                ArrayAdapter<User> adapter = new ArrayAdapter<User>(
                        getContext(),
                        android.R.layout.simple_list_item_1,
                        users
                );

                mUserList.setAdapter(adapter);
                return true;
            }
        });

        LinearLayout mCard = view.findViewById(R.id.CardView);
        mCard.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mCard.setVisibility(View.GONE);
            }
        });

        mPrevButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startCal.add(Calendar.DATE, -1);
                endCal.add(Calendar.DATE, -1);
                reloadData();
            }
        });

        mNextButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startCal.add(Calendar.DATE, 1);
                endCal.add(Calendar.DATE, 1);
                reloadData();
            }
        });
    }

    private void reloadData() {
        // Set progress bar visibility
        mCalendarProgress.setVisibility(View.VISIBLE);

        // Clear list view
        ArrayAdapter<Event> clearEvents = new ArrayAdapter<Event>(
                activity.getApplicationContext(),
                android.R.layout.simple_list_item_1,
                new ArrayList<Event>()
        );

        mList.setAdapter(clearEvents);

        mCurrentDate.setText("" + startCal.get(Calendar.DAY_OF_MONTH) + "." + startCal.get(Calendar.MONTH) + "." + startCal.get(Calendar.YEAR));

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

                    ArrayAdapter<Event> adapter = new ArrayAdapter<Event>(
                            activity.getApplicationContext(),
                            android.R.layout.simple_list_item_1,
                            events
                    );

                    activity.runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            mCalendarProgress.setVisibility(View.GONE);
                            mList.setAdapter(adapter);
                        }
                    });
                } else {
                    Log.e("Get events", json);
                }
            }
        });

    }

}

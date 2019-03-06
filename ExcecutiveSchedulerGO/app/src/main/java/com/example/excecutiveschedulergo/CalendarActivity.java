package com.example.excecutiveschedulergo;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import com.example.Connection.Connection;
import com.example.model.Event;
import com.example.model.User;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.io.IOException;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.Response;

public class CalendarActivity extends AppCompatActivity {

    ListView mList;
    Toolbar  toolbar;
    Connection c = Connection.getInstance();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_calendar);

        mList = findViewById(R.id.list);
        toolbar = new Toolbar(this);

        Date startDate = new Date(0);
        Date endDate = new Date();

        String token = TokenStore.getToken(this.getApplicationContext());
        c.getEvents(startDate, endDate, token, new Callback() {
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
                            CalendarActivity.this,
                            android.R.layout.simple_list_item_1,
                            events
                    );

                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            mList.setAdapter(adapter);
                        }
                    });
                } else {
                    Log.e("Get events", json);
                }
            }
        });
    }

    private void setListeners(){
        mList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                toolbar.event = (Event) parent.getItemAtPosition(position);
            }
        });
    }
}

package com.example.excecutiveschedulergo;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.text.Editable;
import com.example.model.User;
import com.example.Connection.Connection;
import com.example.model.Event;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import java.io.IOException;
import java.lang.reflect.Type;
import java.util.ArrayList;
import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.Response;

/**
 * Share event with other users
 */
public class ShareEventActivity extends AppCompatActivity {

    private EditText mUsername;                         // Input for user search
    private Button mButton;                             // Done button
    private ListView mUserlist, mShareduserlist;        // Lists of users
    private ArrayList<String> sharedusers;              // Array to fill mSharedUserList
    private ArrayList<String> users = new ArrayList<>();// Array to fill mUserList
    private ArrayList<String> usersSearch;              // Array for search results
    private String searchstring = "";                   // String from mUsername

    private Toolbar toolbar;                            // Toolbar inserted
    // Singleton connection
    private Connection c = Connection.getInstance();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_share_event_acticity);
        sharedusers = new ArrayList<>();
        toolbar = new Toolbar(this);
        mUserlist = findViewById(R.id.userlist);

        String token = TokenStore.getToken(this.getApplicationContext());
        c.getAllUsers(token, new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                Log.e("Get Users", "Failed to get users");
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                String json = response.body().string();
                if (response.isSuccessful()) {
                    Gson gson = new Gson();
                    Type type = new TypeToken<ArrayList<User>>(){}.getType();
                    ArrayList<User> user = gson.fromJson(json, type);

                    for(int i = 0; i < user.size(); i++) {
                        users.add(user.get(i).getUsername());
                    }
                    ArrayAdapter<String> adapter = new ArrayAdapter<String>(
                            ShareEventActivity.this,
                            android.R.layout.simple_list_item_1,
                            users
                    );
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            mUserlist.setAdapter(adapter);
                        }
                    });
                } else {
                    // If unauthorized redirect to login.
                    int statusCode = response.code();
                    if (statusCode == 401) {
                        TokenStore.deleteToken(getApplicationContext());
                        Intent login = new Intent(getApplicationContext(), LoginActivity.class);
                        startActivity(login);
                    }
                    Log.e("Share event fail", json);
                }
            }

        });
        setListeners();
    }

    /**
     * Set components and component listeners
     */
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
        mUsername.addTextChangedListener(new TextWatcher() {
            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                searchstring = s.toString().toLowerCase();
                usersSearch = new ArrayList<>();
                Log.e("error", users.toString());
                for(int i = 0; i < users.size(); i++){
                    String uname = users.get(i);
                    if(uname.contains(searchstring)){
                        usersSearch.add(uname);
                    }
                    updateUserList();
                }
            }
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }
            @Override
            public void afterTextChanged(Editable s) {

            }
        });

        mUserlist.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                String user = parent.getItemAtPosition(position).toString();
                if(!sharedusers.contains(user)){
                    sharedusers.add(user);
                    displayShared();
                }
            }
        });

        mShareduserlist = findViewById(R.id.shareduserlist);
        mShareduserlist.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                String s = parent.getItemAtPosition(position).toString();
                sharedusers.remove(s);
                displayShared();
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

    private void displayShared(){
        ArrayAdapter<String> adapter = new ArrayAdapter<String>(
                ShareEventActivity.this,
                android.R.layout.simple_list_item_1,
                sharedusers
        );
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                mShareduserlist.setAdapter(adapter);
            }
        });
    }

    /**
     * Called if there are changes to the user arrays
     */
    private void updateUserList() {
        if(!searchstring.equals("")){
            ArrayAdapter<String> adapter = new ArrayAdapter<String>(
                    ShareEventActivity.this,
                    android.R.layout.simple_list_item_1,
                    usersSearch
            );
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    mUserlist.setAdapter(adapter);
                }
            });
        } else {
            ArrayAdapter<String> adapter = new ArrayAdapter<String>(
                    ShareEventActivity.this,
                    android.R.layout.simple_list_item_1,
                    users
            );
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    mUserlist.setAdapter(adapter);
                }
            });
        }

    }

    /**
     * Update event on backend
     */
    private void shareEvent() {
        String token = TokenStore.getToken(this.getApplicationContext());
        Event event = (Event) (getIntent()).getParcelableExtra("Event");

        c.shareEvent(sharedusers, event, token, new Callback() {
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
                    Log.e("Share event fail", json);
                }
            }
        });
    }

    /**
     * Always go back to FragmentActivity
     */
    public void onBackPressed() {
        Intent calendar = new Intent(getApplicationContext(), FragmentActivity.class);
        startActivity(calendar);
    }
}

package com.example.excecutiveschedulergo;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.Connection.Connection;
import java.io.IOException;
import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.Response;

public class MainActivity extends AppCompatActivity {
    /**
    protected Button mLoginButton;
    private Button mCalendarButton;
    private Button mCreateEventButton;
    private GridView mGridView;
     */

    ImageView mCalendar;
    ImageView mCreate;
    ImageView mLogin;
    private final Connection c = Connection.getInstance();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mCalendar = findViewById(R.id.calendar);
        mCalendar.setImageResource(R.drawable.ic_calendar);

        mCreate = findViewById(R.id.create);
        mCreate.setImageResource(R.drawable.ic_createnewicon);

        mLogin = findViewById(R.id.login);
        mLogin.setImageResource(R.drawable.ic_loginicon);


        /**
        // Create gridview for front page options
        mGridView = findViewById(R.id.the_grid);
        ImageAdapter adapter = new ImageAdapter(this, TokenStore.getToken(getApplicationContext()) != null);
        mGridView.setAdapter(adapter);



        mLoginButton = findViewById(R.id.login_button);
        mCalendarButton = findViewById(R.id.calendar_button);
        mCreateEventButton = findViewById(R.id.create_event_button);


        updateLoginButtonText();

        mLoginButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                adapter.setLogin(TokenStore.getToken(getApplicationContext()) == null);
                if (TokenStore.getToken(getApplicationContext()) != null) {
                    TokenStore.deleteToken(getApplicationContext());
                    updateLoginButtonText();
                }
                else {
                    Intent login = new Intent(getApplicationContext(), LoginActivity.class);
                    startActivity(login);
                }
            }
        });

        mCalendarButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(TokenStore.getToken(getApplicationContext()) == null){
                    Intent login = new Intent(getApplicationContext(), LoginActivity.class);
                    startActivity(login);
                } else{
                    Intent calendar = new Intent(getApplicationContext(), CalendarActivity.class);
                    startActivity(calendar);
                }
            }
        });

        mCreateEventButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(TokenStore.getToken(getApplicationContext()) == null){
                    Intent login = new Intent(getApplicationContext(), LoginActivity.class);
                    startActivity(login);
                } else {
                    Intent createEvent = new Intent(getApplicationContext(), CreateEventActivity.class);
                    Bundle bundle = new Bundle();
                    bundle.putInt("Type", 0);
                    createEvent.putExtras(bundle);
                    startActivity(createEvent);
                }
            }
        });

        Button fragments = findViewById(R.id.fragments);
        fragments.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent createEvent = new Intent(getApplicationContext(), FragmentActivity.class);
                startActivity(createEvent);
            }
        });
         */
    }


    @Override
    protected void onStop(){
        super.onStop();
        Log.e("Main activity on stop: ", "stopped");
    }

    @Override
    protected void onStart() {
        super.onStart();
        setLogin(TokenStore.getToken(getApplicationContext()) == null);
        setListeners();
        //updateLoginButtonText();
    }

    /**
    // Fall sem uppfærir login takkann eftir því hvort notandi sé loggaður inn.
    protected void updateLoginButtonText() {
        if(TokenStore.getToken(getApplicationContext()) == null) {
            mLoginButton.setText("Login");
        }
        else {
            mLoginButton.setText("Logout");
        }
    }
    */

    private void setListeners(){
        mCalendar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(TokenStore.getToken(getApplicationContext()) == null) {
                    Intent login = new Intent(getApplicationContext(), LoginActivity.class);
                    startActivity(login);
                } else {
                    Intent calendar = new Intent(getApplicationContext(), FragmentActivity.class);
                    startActivity(calendar);
                }
            }
        });

        mCreate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(TokenStore.getToken(getApplicationContext()) == null){
                    Intent login = new Intent(getApplicationContext(), LoginActivity.class);
                    startActivity(login);
                } else {
                    Intent createEvent = new Intent(getApplicationContext(), CreateEventActivity.class);
                    Bundle bundle = new Bundle();
                    bundle.putInt("Type", 0);
                    createEvent.putExtras(bundle);
                    startActivity(createEvent);
                }
            }
        });

        mLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                setLogin(TokenStore.getToken(getApplicationContext()) == null);
                if (TokenStore.getToken(getApplicationContext()) != null) {
                    TokenStore.deleteToken(getApplicationContext());
                } else {
                    Intent login = new Intent(getApplicationContext(), LoginActivity.class);
                    startActivity(login);
                }
            }
        });
    }

    public void setLogin(Boolean logged){
        if(logged) {
            mLogin.setImageResource(R.drawable.ic_loginicon);
        } else {
            mLogin.setImageResource(R.drawable.ic_logouticon);
        }
    }
}

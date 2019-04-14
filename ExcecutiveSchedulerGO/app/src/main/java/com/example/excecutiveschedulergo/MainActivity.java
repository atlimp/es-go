package com.example.excecutiveschedulergo;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import com.example.Connection.Connection;

/**
 * Shows options for the user to choose from and
 * sends him to the appropriate activity
 */
public class MainActivity extends AppCompatActivity {

    private ImageView mCalendar;        // Clickable icon for FragmentActivity
    private ImageView mCreate;          // Clickable icon for CreateEventActivity
    private ImageView mLogin;           // Clickable icon for LoginActivity
    // Singleton connection
    private final Connection c = Connection.getInstance();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // Set icons
        mCalendar = findViewById(R.id.calendar);
        mCalendar.setImageResource(R.drawable.ic_calendar);

        mCreate = findViewById(R.id.create);
        mCreate.setImageResource(R.drawable.ic_createnewicon);

        mLogin = findViewById(R.id.login);
        mLogin.setImageResource(R.drawable.ic_loginicon);
    }

    /**
     * Log activity stopping
     */
    @Override
    protected void onStop(){
        super.onStop();
        Log.e("Main activity on stop: ", "stopped");
    }

    /**
     * Set login icons drawable svg and sets component listeners
     */
    @Override
    protected void onStart() {
        super.onStart();
        setLogin(TokenStore.getToken(getApplicationContext()) == null);
        setListeners();
    }

    /**
     * Set component listeners
     */
    private void setListeners(){
        // Check if logged in and open FragmentActivity or redirect to LoginActivity
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
        // Check if logged in and open CreateEventActivity or redirect ot LoginActivity
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
        // Sends user to LoginActivity, if token is not null it is deleted
        mLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (TokenStore.getToken(getApplicationContext()) != null) {
                    TokenStore.deleteToken(getApplicationContext());
                } else {
                    Intent login = new Intent(getApplicationContext(), LoginActivity.class);
                    startActivity(login);
                }
                setLogin(TokenStore.getToken(getApplicationContext()) == null);
            }
        });
    }

    /**
     * Set login icons drawable
     * @param logged
     */
    public void setLogin(Boolean logged){
        if(logged) {
            mLogin.setImageResource(R.drawable.ic_loginicon);
        } else {
            mLogin.setImageResource(R.drawable.ic_logouticon);
        }
    }

    /**
     * Nowhere to go back to
     */
    @Override
    public void onBackPressed() {
        // Nothing here
    }
}

package com.example.excecutiveschedulergo;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import com.example.Connection.Connection;

public class MainActivity extends AppCompatActivity {

    Button mButton;
    Connection connection;
    String username, password;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mButton = findViewById(R.id.button);

        mButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent login = new Intent(getApplicationContext(), LoginActivity.class);

                startActivityForResult(login,1);
            }
        });

        connection = Connection.getInstance();
    }

    // From https://stackoverflow.com/questions/19666572/how-to-call-a-method-in-another-activity-from-activity
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data)
    {
        super.onActivityResult(requestCode, resultCode, data);
        // check if the request code is same as what is passed  here it is 2
        if(requestCode==1)
        {
            Bundle extras = data.getExtras();
            username = extras.getString("Username");
            password = extras.getString("Password");
        }
    }
}

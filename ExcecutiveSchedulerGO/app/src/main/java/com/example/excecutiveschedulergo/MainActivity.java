package com.example.excecutiveschedulergo;

import android.content.Context;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;

import com.example.Connection.Connection;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;

public class MainActivity extends AppCompatActivity {

    Button mButton;
    Connection connection;
    String username, password;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        String[] lastUser = getLastUser();
        if(lastUser.length < 2){
            Intent login = new Intent(getApplicationContext(), LoginActivity.class);
            startActivityForResult(login,1);
        } else {
            // TODO: This should be deleted at some point.
            Log.e("Main activity on create: ", "login info: " + lastUser[0] +","+lastUser[1]);
            username = lastUser[0];
            password = lastUser[1];
        }

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

    @Override
    protected void onStop(){
        super.onStop();
        Log.e("Main activity on stop: ", "stopped");
        setLastUser();
    }

    /**
     * From https://stackoverflow.com/a/39578803
     * When activity has finished and returnes result
     * @param requestCode
     * @param resultCode
     * @param data
     */
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data)
    {
        super.onActivityResult(requestCode, resultCode, data);
        // check if the request code is same as what is passed  here it is 1
        if(requestCode==1)
        {
            Bundle extras = data.getExtras();
            username = extras.getString("Username");
            password = extras.getString("Password");
            connection.setUser(username,password);
        }
    }

    /**
     * Get last user info from file.
     * @return
     */
    private String[] getLastUser(){
        String result[];
        String text = readFromFile(this.getApplicationContext(), "user.txt");
        result = text.split(",");
        return result;
    }

    /**
     * Write current user info to file.
     */
    private void setLastUser(){
        Context context = this.getApplicationContext();
        String data = username + "," + password;
        writeToFile(data,context, "user.txt");
    }

    /**
     * From https://stackoverflow.com/a/14377185
     * Write given data to given filename in given context.
     * @param data
     * @param context
     * @param filename
     */
    private void writeToFile(String data, Context context, String filename) {
        try {
            OutputStreamWriter outputStreamWriter = new OutputStreamWriter(
                    context.openFileOutput(filename, Context.MODE_PRIVATE));
            outputStreamWriter.write(data);
            outputStreamWriter.close();
        }
        catch (IOException e) {
            Log.e("Main activity write to file: ", "File write failed: " + e.toString());
        }
    }

    /**
     * From https://stackoverflow.com/a/14377185
     * Read from given filename in given context.
     * @param context
     * @param filename
     * @return String
     */
    private String readFromFile(Context context, String filename) {

        String result = "";
        try {

            File f = new File(context.getFilesDir(),filename);
            f.createNewFile();

            InputStream inputStream = context.openFileInput(filename);

            if ( inputStream != null ) {
                InputStreamReader inputStreamReader = new InputStreamReader(inputStream);
                BufferedReader bufferedReader = new BufferedReader(inputStreamReader);
                String receiveString = "";
                StringBuilder stringBuilder = new StringBuilder();

                while ( (receiveString = bufferedReader.readLine()) != null ) {
                    stringBuilder.append(receiveString);
                }

                inputStream.close();
                result = stringBuilder.toString();
            }
        }
        catch (FileNotFoundException e) {
            Log.e("Main activity Read from file: ", "File not found: " + e.toString());
        } catch (IOException e) {
            Log.e("Main activity Read from file: ", "Can not read file: " + e.toString());
        }
        return result;
    }

}

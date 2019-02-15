package com.example.Connection;

import android.util.Log;

import com.example.model.User;
import com.google.gson.Gson;

import java.io.IOException;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;
import okio.BufferedSink;

public class Connection {

    private static Connection instance = new Connection();

    private final String URL = "https://executive-scheduler.herokuapp.com";
    private final Gson gson = new Gson();
    private final MediaType JSON = MediaType.parse("application/json");
    private final OkHttpClient client = new OkHttpClient();

    private User user;


    private Connection() {
        user = new User();
    }

    public static Connection getInstance() {
        return instance;
    }


    public void post(String endPoint, Object obj, Callback c) {
        String json = gson.toJson(obj);
        RequestBody body = RequestBody.create(JSON, json);

        Request req = new Request.Builder()
                .addHeader("Authorization", "token")
                .post(body)
                .build();

        Call call = client.newCall(req);

        call.enqueue(c);
    }


    public void get(String endPoint, Callback c) {
        Request req = new Request.Builder().url(URL + endPoint).build();

        Call call = client.newCall(req);

        call.enqueue(c);
    }
    
    public void setUser(String username, String password){
        user.setUsername(username);
        user.setPassword(password);

        // TODO: Connect to backend and fill in the rest of the info.
    }

}

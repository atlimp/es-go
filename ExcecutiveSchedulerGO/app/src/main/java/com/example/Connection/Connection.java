package com.example.Connection;

import com.example.model.User;

import java.io.IOException;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

public class Connection {

    private static Connection instance = new Connection();
    private String URL = "https://apis.is/car";
    private User user;
    private final OkHttpClient client = new OkHttpClient();

    private Connection() {
        user = new User();
    }

    public static Connection getInstance() {
        return instance;
    }

    /*
    public String post(String endPoint, String data) {
        OkHttpClient client = new OkHttpClient();

        Request req = new Request.Builder()
                .url()
    }

    */
    public void get(String endPoint, Callback c) {
        OkHttpClient client = new OkHttpClient();

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

package com.example.Connection;

import android.content.SharedPreferences;
import android.util.Log;

import com.example.excecutiveschedulergo.TokenStore;
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
    private final MediaType JSON = MediaType.parse("application/json");

    private Connection() {

    }

    public static Connection getInstance() {
        return instance;
    }


    public void post(String endPoint, Object obj, Callback c) {
        OkHttpClient client = new OkHttpClient();

        Gson gson = new Gson();
        String json = gson.toJson(obj);
        RequestBody body = RequestBody.create(JSON, json);

        Request req = new Request.Builder()
                .addHeader("Authorization", "token")
                .post(body)
                .url(URL + endPoint)
                .build();

        Call call = client.newCall(req);

        call.enqueue(c);
    }


    public void get(String endPoint, Callback c) {
        OkHttpClient client = new OkHttpClient();

        Request req = new Request.Builder()
                .url(URL + endPoint)
                .build();

        Call call = client.newCall(req);

        call.enqueue(c);
    }

    public void post(String endPoint, Object obj, String token, Callback c) {
        OkHttpClient client = new OkHttpClient();

        Gson gson = new Gson();
        String json = gson.toJson(obj);
        RequestBody body = RequestBody.create(JSON, json);

        Request req = new Request.Builder()
                .addHeader("Authorization", "token")
                .post(body)
                .url(URL + endPoint)
                .addHeader("Authorization", "Bearer " + token)
                .build();

        Call call = client.newCall(req);

        call.enqueue(c);
    }


    public void get(String endPoint, String token, Callback c) {
        OkHttpClient client = new OkHttpClient();

        Request req = new Request.Builder()
                .url(URL + endPoint)
                .addHeader("Authorization", "Bearer " + token)
                .build();

        Call call = client.newCall(req);

        call.enqueue(c);
    }


    public static void main(String[] args) {
        Connection c = Connection.getInstance();
        User user = new User();
        user.setUsername("atli");
        user.setPassword("foo");

        c.post("/login", user, new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                Log.e("Connection main: ", e.getMessage());
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                if (response.isSuccessful()) {
                    String res = response.body().string();
                    Log.e("Connection Successful: ", res);
                } else {
                    String res = response.body().string();
                    Log.e("Connection failed: ", res);
                }
            }
        });
    }

}

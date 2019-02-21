package com.example.Connection;

import android.content.SharedPreferences;
import android.util.Log;

import com.example.excecutiveschedulergo.TokenStore;
import com.example.model.Event;
import com.example.model.User;
import com.google.gson.Gson;

import java.io.IOException;
import java.util.Date;
import java.util.List;

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

    /*
    Connections to backend using OkHTTP to create HTTP requests
    obj is request body
     */


    private void post(String endPoint, Object obj, Callback c) {
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

    private void post(String endPoint, Object obj, String token, Callback c) {
        OkHttpClient client = new OkHttpClient();

        Gson gson = new Gson();
        String json = gson.toJson(obj);
        RequestBody body = RequestBody.create(JSON, json);

        Request req = new Request.Builder()
                .post(body)
                .url(URL + endPoint)
                .addHeader("Authorization", "Bearer " + token)
                .build();

        Call call = client.newCall(req);

        call.enqueue(c);
    }


    private void get(String endPoint, String token, Callback c) {
        OkHttpClient client = new OkHttpClient();

        Request req = new Request.Builder()
                .url(URL + endPoint)
                .addHeader("Authorization", "Bearer " + token)
                .build();

        Call call = client.newCall(req);

        call.enqueue(c);
    }

    private void delete(String endPoint, String token, Callback c) {
        OkHttpClient client = new OkHttpClient();

        Request req = new Request.Builder()
                .delete()
                .url(URL + endPoint)
                .addHeader("Authorization", "Bearer " + token)
                .build();

        Call call = client.newCall(req);

        call.enqueue(c);
    }

    public void loginUser(User user, Callback c) {
        this.post("/login", user, c);
    }

    public void registerUser(User user, Callback c) {
        this.post("/register", user, c);
    }

    public void createEvent(Event event, String token, Callback c) {
        this.post("/event", event, token, c);
    }

    public void editEvent(Event event, String token, Callback c) {
        String endPoint = "/event/" + event.getId();
        this.post(endPoint, event, token, c);
    }

    public void deleteEvent(Event event, String token, Callback c) {
        String endPoint = "/event/" + event.getId();
        this.post(endPoint, token, c);
    }

    public void getEvents(Date startDate, Date endDate, String token, Callback c) {
        String endPoint = "/event?startDate=" +
                startDate.getTime() +
                "&endDate=" +
                endDate.getTime();

        this.get(endPoint, token, c);
    }

    public void getEventByID(Long id, String token, Callback c) {
        String endPoint = "/event/" + id;
        this.get(endPoint, token, c);
    }

    public void shareEvent(List<User> users, Event event, String token, Callback c) {
        String endPoint = "/event/" + event.getId();
        this.post(endPoint, users, token, c);
    }

    public void deleteUser(String token, Callback c) {
        this.delete("/user", token, c);
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

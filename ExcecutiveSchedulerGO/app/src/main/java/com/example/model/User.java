package com.example.model;

import android.os.Parcel;
import android.os.Parcelable;

import java.util.ArrayList;
import java.util.List;

public class User implements Parcelable {

    private Long id;
    private String name;
    private String username;
    private String password;
    private String token;
    private List<Event> events;

    public User()  {

    }

    public User(String username, String name, String password, String token) {
        this.username = username;
        this.name = name;
        this.password = password;
        this.token = token;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getToken() {
        return token;
    }

    public void setToken(String token) {
        this.token = token;
    }

    public List<Event> getEvents() { return events; }

    public void setEvents(List<Event> events) {
        this.events = events;
    }

    /**
     * Parcelable stuff
     * @return int for identification if necessary.
     */
    @Override
    public int describeContents(){
        return 0;
    }

    @Override
    public void writeToParcel(Parcel out, int flags){
        out.writeLong(id);
        out.writeString(name);
        out.writeString(username);
        out.writeString(password);
        out.writeString(token);
        out.writeList(events);
    }

    public static final Parcelable.Creator<User> CREATOR = new Parcelable.Creator<User>(){

        public User createFromParcel(Parcel in){
            return new User(in.readString(),in.readString(),in.readString(),in.readString());
        }

        public User[] newArray(int size){
            return new User[size];
        }
    };
}

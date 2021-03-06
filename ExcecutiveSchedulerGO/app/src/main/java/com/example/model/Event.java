package com.example.model;

import android.os.Parcel;
import android.os.Parcelable;

import java.text.DateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;

/**
 * Model class for Event
 */
public class Event implements Parcelable {

    private Long id;
    private Date startDate;
    private Date endDate;
    private String title;
    private String description;
    private List<User> users;

    /**
     * Empty constructor to create empty event
     */
    public Event() {

    }

    /**
     * Constructor for transfer from backend
     * @param id
     * @param startDate
     * @param endDate
     * @param title
     * @param description
     * @param users
     */
    public Event(Long id, Date startDate, Date endDate, String title, String description, List<User> users) {
        this.id = id;
        this.startDate = startDate;
        this.endDate = endDate;
        this.title = title;
        this.description = description;
        this.users = users;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Date getStartDate() {
        return startDate;
    }

    public void setStartDate(Date startDate) {
        this.startDate = startDate;
    }

    public Date getEndDate() {
        return endDate;
    }

    public void setEndDate(Date endDate) {
        this.endDate = endDate;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public List<User> getUsers() {
        return users;
    }

    public void setUsers(List<User> users) {
        this.users = users;
    }

    /**
     * This is for the ListView widget in the calendar activity
     * @return
     */
    public String toString() {
        Locale loc = new Locale("is", "IS");
        DateFormat timeFormat = DateFormat.getTimeInstance(DateFormat.SHORT, loc);
        return this.title + "\n\n" +
                timeFormat.format(this.startDate) + " - " + timeFormat.format(this.endDate);
    }

    /**
     * Parcelable stuff.
     * @return int for identification if necessary
     */
    @Override
    public int describeContents(){
        return 0;
    }

    /**
     * Must be in same order as createFromParcel
     * @param out
     * @param flags
     */
    @Override
    public void writeToParcel(Parcel out,  int flags){
        out.writeLong(id);
        out.writeLong(startDate.getTime());
        out.writeLong(endDate.getTime());
        out.writeString(title);
        out.writeString(description);
        out.writeList(users);
    }

    public static final Parcelable.Creator<Event> CREATOR = new Parcelable.Creator<Event>(){

      public Event createFromParcel(Parcel in){
          long inId = in.readLong();
          Date inStartDate   = new Date(in.readLong());
          Date inEndDate     = new Date(in.readLong());
          String inTitle     = in.readString();
          String inDescription = in.readString();
          ArrayList<User> inUsers = new ArrayList<>();
          in.readList(inUsers, User.class.getClassLoader());

          return new Event(inId,inStartDate,inEndDate,inTitle,inDescription,inUsers);
      }

      public Event[] newArray(int size){
          return new Event[size];
      }
    };
}

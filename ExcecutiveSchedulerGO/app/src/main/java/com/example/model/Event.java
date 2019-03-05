package com.example.model;

import java.text.DateFormat;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class Event {

    private Long id;
    private Date startDate;
    private Date endDate;
    private String title;
    private String description;
    private List<User> users;

    public Event() {

    }

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
        DateFormat dateFormat = DateFormat.getDateInstance(DateFormat.DEFAULT, loc);

        return this.title + "\n" +
                dateFormat.format(this.startDate) + " - " + dateFormat.format(this.endDate);
    }
}

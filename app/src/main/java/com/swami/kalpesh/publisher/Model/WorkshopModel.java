package com.swami.kalpesh.publisher.Model;

import java.sql.Timestamp;

public class WorkshopModel {

    private String faculty_name;
    private String name_of_workshop;
    private String organized_by;
    private String duration;
    private String start_date;
    private String end_date;
    private String Acadmic_year;
    private String username;
    private long timestamp;
    private String emailid;
    private String Workshop;

    public WorkshopModel() {

    }

    public WorkshopModel(String name_of_workshop, String organized_by, String duration, String start_date, String end_date, String acadmic_year, String username) {
        this.name_of_workshop = name_of_workshop;
        this.organized_by = organized_by;
        this.duration = duration;
        this.start_date = start_date;
        this.end_date = end_date;
        Acadmic_year = acadmic_year;
        this.username = username;
    }

    public WorkshopModel(String name_of_workshop, String organized_by, String duration, String start_date, String end_date, String acadmic_year) {
        this.name_of_workshop = name_of_workshop;
        this.organized_by = organized_by;
        this.duration = duration;
        this.start_date = start_date;
        this.end_date = end_date;
        Acadmic_year = acadmic_year;
    }

    public String getName_of_workshop() {
        return name_of_workshop;
    }

    public void setName_of_workshop(String name_of_workshop) {
        this.name_of_workshop = name_of_workshop;
    }

    public String getOrganized_by() {
        return organized_by;
    }

    public void setOrganized_by(String organized_by) {
        this.organized_by = organized_by;
    }

    public String getDuration() {
        return duration;
    }

    public void setDuration(String duration) {
        this.duration = duration;
    }

    public String getStart_date() {
        return start_date;
    }

    public void setStart_date(String start_date) {
        this.start_date = start_date;
    }

    public String getEnd_date() {
        return end_date;
    }

    public void setEnd_date(String end_date) {
        this.end_date = end_date;
    }

    public String getAcadmic_year() {
        return Acadmic_year;
    }

    public void setAcadmic_year(String acadmic_year) {
        Acadmic_year = acadmic_year;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public long getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(long timestamp) {
        this.timestamp = timestamp;
    }

    public String getFaculty_name() {
        return faculty_name;
    }

    public void setFaculty_name(String faculty_name) {
        this.faculty_name = faculty_name;
    }

    public String getEmailid() {
        return emailid;
    }

    public void setEmailid(String emailid) {
        this.emailid = emailid;
    }

}

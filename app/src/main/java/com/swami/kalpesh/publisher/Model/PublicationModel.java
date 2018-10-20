package com.swami.kalpesh.publisher.Model;

import java.sql.Timestamp;

public class PublicationModel {

    private String name_of_faculy;
    private String name_of_author;
    private String type_of_publication;
    private String title_of_paper;
    private String name_of_conference;
    private String date_of_publication;
    private String DOI;
    private String Year_of_publication;
    private long timestamp;
    private String emailid;

    public PublicationModel() {
    }

    public PublicationModel(String name_of_faculy, String name_of_author, String type_of_publication, String title_of_paper, String name_of_conference, String date_of_publication, String DOI, String year_of_publication) {
        this.name_of_faculy = name_of_faculy;
        this.name_of_author = name_of_author;
        this.type_of_publication = type_of_publication;
        this.title_of_paper = title_of_paper;
        this.name_of_conference = name_of_conference;
        this.date_of_publication = date_of_publication;
        this.DOI = DOI;
        Year_of_publication = year_of_publication;
    }

    public String getName_of_faculy() {
        return name_of_faculy;
    }

    public void setName_of_faculy(String name_of_faculy) {
        this.name_of_faculy = name_of_faculy;
    }

    public String getName_of_author() {
        return name_of_author;
    }

    public void setName_of_author(String name_of_author) {
        this.name_of_author = name_of_author;
    }

    public String getType_of_publication() {
        return type_of_publication;
    }

    public void setType_of_publication(String type_of_publication) {
        this.type_of_publication = type_of_publication;
    }

    public String getTitle_of_paper() {
        return title_of_paper;
    }

    public void setTitle_of_paper(String title_of_paper) {
        this.title_of_paper = title_of_paper;
    }

    public String getName_of_conference() {
        return name_of_conference;
    }

    public void setName_of_conference(String name_of_conference) {
        this.name_of_conference = name_of_conference;
    }

    public String getDate_of_publication() {
        return date_of_publication;
    }

    public void setDate_of_publication(String date_of_publication) {
        this.date_of_publication = date_of_publication;
    }

    public String getDOI() {
        return DOI;
    }

    public void setDOI(String DOI) {
        this.DOI = DOI;
    }

    public String getYear_of_publication() {
        return Year_of_publication;
    }

    public void setYear_of_publication(String year_of_publication) {
        Year_of_publication = year_of_publication;
    }

    public long getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(long timestamp) {
        this.timestamp = timestamp;
    }

    public String getEmailid() {
        return emailid;
    }

    public void setEmailid(String emailid) {
        this.emailid = emailid;
    }
}

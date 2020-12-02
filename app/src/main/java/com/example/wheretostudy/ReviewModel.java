package com.example.wheretostudy;

public class ReviewModel {
    private String Username, Comment, Rate, Date;

    public ReviewModel() {
    }

    public ReviewModel(String Comment, String Date, String Rate, String Username) {
        this.Username = Username;
        this.Comment = Comment;
        this.Rate = Rate;
        this.Date = Date;
    }

    public String getUserName() {
        return Username;
    }

    public void setUserName(String Username) {
        this.Username = Username;
    }

    public String getComment() {
        return Comment;
    }

    public void setComment(String Comment) {
        this.Comment = Comment;
    }

    public String getRate() {
        return Rate;
    }

    public void setRate(String Rate) {
        this.Rate = Rate;
    }

    public String getDate() {
        return Date;
    }

    public void setDate(String Date) {
        this.Date = Date;
    }
}

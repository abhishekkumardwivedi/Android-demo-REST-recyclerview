package com.trucklancer.trucklancer.bids;

/**
 * Created by Lincoln on 15/01/16.
 */
public class Load {
    private String title;
    private String genre;
    private String year;
    private String budget;
    private String vehicle;

    public Load() {
    }


    public Load(String title, String genre, String year) {
        this.title = title;
        this.genre = genre;
        this.year = year;
        this.budget = null;
        this.vehicle = null;
    }

    public Load(String title, String genre, String year, String budget) {
        this.title = title;
        this.genre = genre;
        this.year = year;
        this.budget = budget;
        this.vehicle = null;
    }

    public Load(String title, String genre, String year, String budget, String vehicle) {
        this.title = title;
        this.genre = genre;
        this.year = year;
        this.budget = budget;
        this.vehicle = vehicle;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String name) {
        this.title = name;
    }

    public String getYear() {
        return year;
    }

    public void setYear(String year) {
        this.year = year;
    }

    public String getGenre() {
        return genre;
    }

    public void setGenre(String genre) {
        this.genre = genre;
    }

    public void setBudget(String budget) {
        this.budget = budget;
    }

    public String getBudget() {
        return budget;
    }

    public void setVehicle(String vehicle) {
        this.vehicle = vehicle;
    }

    public String getVehicle() {
        return vehicle;
    }
}

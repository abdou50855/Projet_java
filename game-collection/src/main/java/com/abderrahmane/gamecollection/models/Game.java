package com.abderrahmane.gamecollection.models;

import javafx.beans.property.*;

public class Game {

    private final IntegerProperty id;
    private final StringProperty title;
    private final IntegerProperty year;
    private final StringProperty platformName;
    private final StringProperty imagePath;

    public Game(int id, String title, int year, String platformName, String imagePath) {
        this.id = new SimpleIntegerProperty(id);
        this.title = new SimpleStringProperty(title);
        this.year = new SimpleIntegerProperty(year);
        this.platformName = new SimpleStringProperty(platformName);
        this.imagePath = new SimpleStringProperty(imagePath);
    }

    // GETTERS
    public int getId() { return id.get(); }
    public String getTitle() { return title.get(); }
    public int getYear() { return year.get(); }
    public String getPlatformName() { return platformName.get(); }
    public String getImagePath() { return imagePath.get(); }

    // PROPERTIES (JavaFX)
    public IntegerProperty idProperty() { return id; }
    public StringProperty titleProperty() { return title; }
    public IntegerProperty yearProperty() { return year; }
    public StringProperty platformNameProperty() { return platformName; }
    public StringProperty imagePathProperty() { return imagePath; }

    // SETTERS (nécessaires pour CRUD)
    public void setTitle(String title) { this.title.set(title); }
    public void setYear(int year) { this.year.set(year); }
    public void setPlatformName(String platformName) { this.platformName.set(platformName); }
    public void setImagePath(String imagePath) { this.imagePath.set(imagePath); }
}

package com.abderrahmane.gamecollection.models;

import javafx.beans.property.*;

public class Platform {

    private final IntegerProperty id;
    private final StringProperty name;

    public Platform(int id, String name) {
        this.id = new SimpleIntegerProperty(id);
        this.name = new SimpleStringProperty(name);
    }

    public Platform(String name) {
        this.id = new SimpleIntegerProperty(0);
        this.name = new SimpleStringProperty(name);
    }

    public int getId() { return id.get(); }
    public IntegerProperty idProperty() { return id; }

    public String getName() { return name.get(); }
    public StringProperty nameProperty() { return name; }

    @Override
    public String toString() {
        return getName();
    }
}

package org.immagixe.weatherviewer.openWeather.models;

public class LocationWeather {

    private String name;
    private Temperature main;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Temperature getMain() {
        return main;
    }

    public void setMain(Temperature main) {
        this.main = main;
    }
}

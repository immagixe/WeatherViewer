package org.immagixe.weatherviewer.openWeather.models;

public class LocationWeather {

    private Temperature main;

    public Temperature getMain() {
        return main;
    }

    public void setMain(Temperature main) {
        this.main = main;
    }

    @Override
    public String toString() {
        return "LocationWeather{" +
                "main=" + main.toString() +
                '}';
    }
}

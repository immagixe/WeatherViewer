package org.immagixe.weatherviewer.openWeather.models;

public class LocationWeather {

    private String name;
    private MainParameters main;
    private WindSpeed wind;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public MainParameters getMain() {
        return main;
    }

    public void setMain(MainParameters main) {
        this.main = main;
    }

    public WindSpeed getWind() {
        return wind;
    }

    public void setWind(WindSpeed wind) {
        this.wind = wind;
    }
}

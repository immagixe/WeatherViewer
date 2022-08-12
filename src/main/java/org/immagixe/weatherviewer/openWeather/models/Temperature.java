package org.immagixe.weatherviewer.openWeather.models;

public class Temperature {
    private double temp;

    public double getTemp() {
        return temp;
    }

    public void setTemp(double temp) {
        this.temp = temp;
    }

    @Override
    public String toString() {
        return "Temperature{" +
                "temp='" + temp + '\'' +
                '}';
    }
}

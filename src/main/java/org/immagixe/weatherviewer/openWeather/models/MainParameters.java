package org.immagixe.weatherviewer.openWeather.models;

public class MainParameters {

    private double temp;
    private int humidity;

    public int getHumidity() {
        return humidity;
    }

    public void setHumidity(int humidity) {
        this.humidity = humidity;
    }

    public double getTemp() {
        return temp;
    }

    public void setTemp(double temp) {
        this.temp = temp;
    }

    public String getTemperature() {
        int temperature = (int) temp;

        if (temperature > 0) {
            return "+" + temperature;
        } else if (temperature < 0) {
            return "-" + temperature;
        } else {
            return String.valueOf(temperature);
        }
    }
}

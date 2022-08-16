package org.immagixe.weatherviewer.openWeather;

import org.immagixe.weatherviewer.models.Location;
import org.immagixe.weatherviewer.openWeather.models.LocationWeather;
import org.immagixe.weatherviewer.openWeather.models.SearchResult;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.HttpServerErrorException;
import org.springframework.web.client.RestTemplate;

import java.net.URI;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;

@Service
public class RestApiService {

    private final static String APP_ID = "4055719026302533a46ba66f4d6d4f98";
    private final static String API_SERVICE = "https://api.openweathermap.org/data/2.5/weather";

    public SearchResult searchLocation(Location location) {

        RestTemplate restTemplate = new RestTemplate();
        String locationName = location.getName();

        String urlencoded = API_SERVICE + "?q=" +
                URLEncoder.encode(locationName, StandardCharsets.UTF_8) +
                "&appid=" +
                URLEncoder.encode(APP_ID, StandardCharsets.UTF_8) +
                "&units=" +
                URLEncoder.encode("metric", StandardCharsets.UTF_8);
        URI uri = URI.create(urlencoded);

        SearchResult response = null;
        try {
            response = restTemplate.getForObject(uri, SearchResult.class);
        } catch (HttpClientErrorException | HttpServerErrorException httpClientOrServerExc) {

            if (HttpStatus.NOT_FOUND.equals(httpClientOrServerExc.getStatusCode())) {
                return null;
            }
        }
        return response;
    }

    public LocationWeather getWeatherByCoordinates(Location location) {

        RestTemplate restTemplate = new RestTemplate();
        String latitude = String.valueOf(location.getLatitude());
        String longitude = String.valueOf(location.getLongitude());

        String urlencoded = API_SERVICE + "?lat=" +
                URLEncoder.encode(latitude, StandardCharsets.UTF_8) +
                "&lon=" +
                URLEncoder.encode(longitude, StandardCharsets.UTF_8) +
                "&appid=" +
                URLEncoder.encode(APP_ID, StandardCharsets.UTF_8) +
                "&units=" +
                URLEncoder.encode("metric", StandardCharsets.UTF_8);
        URI uri = URI.create(urlencoded);

        LocationWeather response = null;
        try {
            response = restTemplate.getForObject(uri, LocationWeather.class);
        } catch (Exception e) {
            e.printStackTrace();
        }

        return response;
    }

    public List<LocationWeather> getLocationWeatherList(List<Location> locations) {
        List<LocationWeather> locationWeatherList = new ArrayList<>();
        for (Location location : locations) {
            locationWeatherList.add(getWeatherByCoordinates(location));
        }
        return locationWeatherList;
    }
}

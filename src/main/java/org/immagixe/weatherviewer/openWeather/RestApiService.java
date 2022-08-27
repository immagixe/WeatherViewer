package org.immagixe.weatherviewer.openWeather;

import org.immagixe.weatherviewer.models.Location;
import org.immagixe.weatherviewer.openWeather.models.LocationWeather;
import org.immagixe.weatherviewer.openWeather.models.SearchResult;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.HttpServerErrorException;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;

import java.net.URI;
import java.util.*;
import java.util.stream.Collectors;

@Service
public class RestApiService {

    private final static String APP_ID = "4055719026302533a46ba66f4d6d4f98";
    private final static String API_SERVICE = "https://api.openweathermap.org/data/2.5/weather";

    public SearchResult searchLocation(Location location) {

        RestTemplate restTemplate = new RestTemplate();
        String locationName = location.getName();

        String urlencoded = UriComponentsBuilder.fromHttpUrl(API_SERVICE)
                .queryParam("q", locationName)
                .queryParam("appid", APP_ID)
                .queryParam("units", "metric")
                .encode()
                .toUriString();

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

        String urlencoded = UriComponentsBuilder.fromHttpUrl(API_SERVICE)
                .queryParam("lat", latitude)
                .queryParam("lon", longitude)
                .queryParam("appid", APP_ID)
                .queryParam("units", "metric")
                .encode()
                .toUriString();

        URI uri = URI.create(urlencoded);

        LocationWeather response = null;
        try {
            response = restTemplate.getForObject(uri, LocationWeather.class);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return response;
    }

    public Map<Integer, LocationWeather> getLocationWeatherLinkedHashMap(List<Location> locations) {
        return locations.stream()
                .collect(Collectors.toMap(Location::getId, this::getWeatherByCoordinates, (v1, v2) -> {
                    throw new IllegalStateException("Unexpected duplicate values:" + Arrays.asList(v1, v2));
                }, LinkedHashMap::new));
    }
}


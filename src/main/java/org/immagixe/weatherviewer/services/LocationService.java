package org.immagixe.weatherviewer.services;

import org.immagixe.weatherviewer.models.Location;
import org.immagixe.weatherviewer.models.User;
import org.immagixe.weatherviewer.openWeather.models.SearchResult;
import org.immagixe.weatherviewer.repositories.LocationRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@Transactional(readOnly = true)
public class LocationService {

    private final LocationRepository locationRepository;

    @Autowired
    public LocationService(LocationRepository locationRepository) {
        this.locationRepository = locationRepository;
    }

    public List<Location> getLocationList(User user) {
        return locationRepository.findByUser(user);
    }

    @Transactional
    public void save(Location location) {
        locationRepository.save(location);
    }

    public Location findByNameAndUser(Location location) {
        String locationName = location.getName();
        User user = location.getUser();
        return locationRepository.findByNameAndUser(locationName, user).orElse(null);
    }

    public void setCoordinates(SearchResult searchResult, Location location) {
        location.setLatitude(searchResult.getCoord().getLat());
        location.setLongitude(searchResult.getCoord().getLon());
    }

    public void setUser(User user, Location location) {
        location.setUser(user);
    }
}

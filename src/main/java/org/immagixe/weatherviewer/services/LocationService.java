package org.immagixe.weatherviewer.services;

import org.immagixe.weatherviewer.models.Location;
import org.immagixe.weatherviewer.repositories.LocationRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional(readOnly = true)
public class LocationService {

    private final LocationRepository locationRepository;

    @Autowired
    public LocationService(LocationRepository locationRepository) {
        this.locationRepository = locationRepository;
    }

    @Transactional
    public void save (Location location) {
        locationRepository.save(location);
    }
}

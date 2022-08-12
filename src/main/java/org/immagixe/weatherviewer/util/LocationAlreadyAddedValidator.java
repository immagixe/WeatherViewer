package org.immagixe.weatherviewer.util;

import org.immagixe.weatherviewer.models.Location;
import org.immagixe.weatherviewer.services.LocationService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.validation.Errors;
import org.springframework.validation.Validator;

@Component
public class LocationAlreadyAddedValidator implements Validator {

    private final LocationService locationService;

    @Autowired
    public LocationAlreadyAddedValidator(LocationService locationService) {
        this.locationService = locationService;
    }

    @Override
    public boolean supports(Class<?> clazz) {
        return Location.class.equals(clazz);
    }

    @Override
    public void validate(Object target, Errors errors) {
        Location location = (Location) target;

        if (locationService.findByNameAndUser(location) != null) {
            errors.rejectValue("name", "", "Location has already been added to the list");
        }
    }
}

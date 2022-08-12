package org.immagixe.weatherviewer.util;

import org.immagixe.weatherviewer.openWeather.models.SearchResult;
import org.springframework.stereotype.Component;
import org.springframework.validation.Errors;
import org.springframework.validation.Validator;

@Component
public class LocationExistValidator implements Validator {

    @Override
    public boolean supports(Class<?> clazz) {
        return SearchResult.class.equals(clazz);
    }

    @Override
    public void validate(Object target, Errors errors) {
        SearchResult searchResult = (SearchResult) target;

        //noinspection ConstantConditions
        if (searchResult == null) {
            errors.rejectValue("name", "", "City not found");
        }
    }
}

package org.immagixe.weatherviewer.controllers;

import org.immagixe.weatherviewer.models.Location;
import org.immagixe.weatherviewer.models.User;
import org.immagixe.weatherviewer.openWeather.models.LocationWeather;
import org.immagixe.weatherviewer.openWeather.models.SearchResult;
import org.immagixe.weatherviewer.services.LocationService;
import org.immagixe.weatherviewer.openWeather.RestApiService;
import org.immagixe.weatherviewer.services.SessionService;
import org.immagixe.weatherviewer.services.UserService;
import org.immagixe.weatherviewer.util.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletResponse;
import javax.validation.Valid;
import java.util.List;
import java.util.Map;

@Controller
public class WeatherController {

    private final UserService userService;
    private final SessionService sessionService;
    private final LocationService locationService;
    private final RestApiService restApiService;
    private final UserValidator userValidator;
    private final PasswordValidator passwordValidator;
    private final LocationExistValidator locationExistValidator;
    private final LocationAlreadyAddedValidator locationAlreadyAddedValidator;

    @Autowired
    public WeatherController(UserService userService, SessionService sessionService, UserValidator userValidator, PasswordValidator passwordValidator, LocationService locationService, RestApiService restApiService, LocationExistValidator locationExistValidator, LocationAlreadyAddedValidator locationAlreadyAddedValidator, BCryptPassword bCryptPassword) {
        this.userService = userService;
        this.sessionService = sessionService;
        this.userValidator = userValidator;
        this.passwordValidator = passwordValidator;
        this.locationService = locationService;
        this.restApiService = restApiService;
        this.locationExistValidator = locationExistValidator;
        this.locationAlreadyAddedValidator = locationAlreadyAddedValidator;
    }

    @GetMapping("/")
    public String mainPage(@CookieValue(value = "session_id", defaultValue = "") String sessionUuid, Model model) {
        if (!sessionUuid.equals("")) {
            User user = sessionService.getUser(sessionUuid);
            List<Location> locations = locationService.getLocationList(user);
            Map<Integer, LocationWeather> locationWeatherList = restApiService.getLocationWeatherLinkedHashMap(locations);

            model.addAttribute("locationWeather", locationWeatherList);
            model.addAttribute("login", sessionService.getAuthorizedLogin(sessionUuid));
        }
        model.addAttribute("location", new Location());

        return "main";
    }

    @GetMapping("/new")
    public String registration(@CookieValue(value = "session_id", defaultValue = "")
                               String sessionUuid, Model model) {
        if (!sessionUuid.equals(""))
            return "redirect:/";
        model.addAttribute("user", new User());

        return "registration";
    }

    @PostMapping("/new")
    public String registration(@ModelAttribute @Valid User user, BindingResult bindingResult) {
        userValidator.validate(user, bindingResult);
        if (bindingResult.hasErrors())
            return "registration";

        String securedPassword = BCryptPassword.getSecuredPasswordHash(user.getPassword());
        user.setPassword(securedPassword);
        userService.save(user);
        return "redirect:/login";
    }

    @GetMapping("/login")
    public String enterLoginAndPassword(@CookieValue(value = "session_id", defaultValue = "")
                                        String sessionUuid, Model model) {
        if (!sessionUuid.equals(""))
            return "redirect:/";
        model.addAttribute("user", new User());

        return "authorization";
    }

    @PostMapping("/login")
    public String authorization(@ModelAttribute("user") @Valid User user,
                                BindingResult bindingResult, HttpServletResponse response) {
        // Проверка на корректность ввода
        if (bindingResult.hasErrors())
            return "authorization";

        // Проверка на существование аккаунта, если первая проверка прошла успешно
        passwordValidator.validate(user, bindingResult);
        if (bindingResult.hasErrors())
            return "authorization";

        User foundUser = userService.findByLoginAndPassword(user);
        String uuid = sessionService.saveSessionAndGetUuid(foundUser).toString();

        Cookie cookie = new Cookie("session_id", uuid);
        cookie.setMaxAge((int) (SessionService.SESSION_LIFE_TIME / 1000));
        response.addCookie(cookie);

        return "redirect:/login";
    }

    @GetMapping("/logout")
    public String deleteCookies(HttpServletResponse response) {
        Cookie cookie = new Cookie("session_id", "");
        cookie.setMaxAge(0);
        response.addCookie(cookie);

        return "redirect:/";
    }

    @GetMapping("/locations")
    public String findLocations(@CookieValue(value = "session_id", defaultValue = "") String sessionUuid,
                                @ModelAttribute("location") @Valid Location location, BindingResult bindingResult,
                                HttpServletResponse response, Model model) {
        SearchResult searchResult = restApiService.searchLocation(location);
        locationExistValidator.validate(searchResult, bindingResult);
        if (bindingResult.hasErrors())
            return "locations";

        locationService.setCoordinates(searchResult, location);
        if (!sessionUuid.equals("")) {
            if (!sessionService.isExpired(sessionUuid)) {
                model.addAttribute("login", sessionService.getAuthorizedLogin(sessionUuid));
            } else {
                response.addCookie(sessionService.cleanCookie());
                return "redirect:/locations";
            }
        }
        model.addAttribute("location", location);
        return "locations";
    }

    @PostMapping("/add")
    public String addLocation(@CookieValue(value = "session_id", defaultValue = "") String sessionUuid,
                              @ModelAttribute @Valid Location location, BindingResult bindingResult,
                              HttpServletResponse response, Model model) {
        if (!sessionService.isExpired(sessionUuid)) {
            model.addAttribute("login", sessionService.getAuthorizedLogin(sessionUuid));
            locationService.setUser(sessionService.getUser(sessionUuid), location);

            locationAlreadyAddedValidator.validate(location, bindingResult);
            if (bindingResult.hasErrors())
                return "locations";
        } else {
            response.addCookie(sessionService.cleanCookie());
            return "redirect:/locations";
        }
        locationService.save(location);

        return "redirect:/";
    }

    @DeleteMapping("/delete")
    public String deleteLocation(@CookieValue(value = "session_id", defaultValue = "") String sessionUuid,
                                 @ModelAttribute("locationToDelete") int locationId,
                                 HttpServletResponse response, Model model) {
        if (!sessionService.isExpired(sessionUuid)) {
            User user = sessionService.getUser(sessionUuid);
            userService.deleteLocationFromList(user, locationId);

            model.addAttribute("login", sessionService.getAuthorizedLogin(sessionUuid));
            return "redirect:/";
        } else {
            response.addCookie(sessionService.cleanCookie());
        }
        return "redirect:/";
    }

    @DeleteMapping("/deleteall")
    public String deleteAllLocations(@CookieValue(value = "session_id", defaultValue = "") String sessionUuid,
                                     @ModelAttribute("locationToDelete") String locationName,
                                     HttpServletResponse response, Model model) {
        if (!sessionService.isExpired(sessionUuid)) {
            User user = sessionService.getUser(sessionUuid);
            userService.deleteAllLocations(user);

            model.addAttribute("login", sessionService.getAuthorizedLogin(sessionUuid));
            return "redirect:/";
        } else {
            response.addCookie(sessionService.cleanCookie());
        }
        return "redirect:/";
    }
}

package org.immagixe.weatherviewer.controllers;

import org.immagixe.weatherviewer.models.Location;
import org.immagixe.weatherviewer.models.Session;
import org.immagixe.weatherviewer.models.User;
import org.immagixe.weatherviewer.openWeather.models.SearchResult;
import org.immagixe.weatherviewer.services.LocationService;
import org.immagixe.weatherviewer.openWeather.RestApiService;
import org.immagixe.weatherviewer.services.SessionService;
import org.immagixe.weatherviewer.services.UserService;
import org.immagixe.weatherviewer.util.LocationAlreadyAddedValidator;
import org.immagixe.weatherviewer.util.LocationExistValidator;
import org.immagixe.weatherviewer.util.PasswordValidator;
import org.immagixe.weatherviewer.util.UserValidator;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import javax.validation.Valid;

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
    public WeatherController(UserService userService, SessionService sessionService, UserValidator userValidator, PasswordValidator passwordValidator, LocationService locationService, RestApiService restApiService, LocationExistValidator locationExistValidator, LocationAlreadyAddedValidator locationAlreadyAddedValidator) {
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
            model.addAttribute("login", sessionService.getAuthorizedLogin(sessionUuid));
        }
        model.addAttribute("location", new Location());


//        LocationWeather locationWeather = restApiService.getWeatherByCoordinates(locationWithCoordinates);
//
//        System.out.println(locationWeather.getMain().getTemp());
//        System.out.println(locationWithCoordinates.getLatitude());
//        System.out.println(locationWithCoordinates.getLongitude());

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

        userService.save(user);
        return "authorization";
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
        if (bindingResult.hasErrors())
            return "authorization";

        passwordValidator.validate(user, bindingResult);
        if (bindingResult.hasErrors())
            return "authorization";

        User foundUser = userService.findByLoginAndPassword(user);
        String uuid = sessionService.saveSessionAndGetUuid(foundUser).toString();

        Cookie cookie = new Cookie("session_id", uuid);
        cookie.setMaxAge((int) (SessionService.SESSION_LIFE_TIME / 1000));
        response.addCookie(cookie);

        return "redirect:/";
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
            Cookie cookie = new Cookie("session_id", "");
            cookie.setMaxAge(0);
            response.addCookie(cookie);
            return "redirect:/locations";
        }
        locationService.save(location);

        return "redirect:/";
    }
}

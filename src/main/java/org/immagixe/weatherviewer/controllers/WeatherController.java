package org.immagixe.weatherviewer.controllers;

import org.immagixe.weatherviewer.models.User;
import org.immagixe.weatherviewer.services.LocationService;
import org.immagixe.weatherviewer.services.SessionService;
import org.immagixe.weatherviewer.services.UsersService;
import org.immagixe.weatherviewer.util.PasswordValidator;
import org.immagixe.weatherviewer.util.UserValidator;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.CookieValue;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import javax.validation.Valid;

@Controller
public class WeatherController {

    private final UsersService usersService;
    private final SessionService sessionService;
    private final LocationService locationService;

    private final UserValidator userValidator;
    private final PasswordValidator passwordValidator;

    @Autowired
    public WeatherController(UsersService usersService, SessionService sessionService, UserValidator userValidator, PasswordValidator passwordValidator, LocationService locationService) {
        this.usersService = usersService;
        this.sessionService = sessionService;
        this.userValidator = userValidator;
        this.passwordValidator = passwordValidator;
        this.locationService = locationService;
    }

    @GetMapping("/")
    public String mainPage(@CookieValue(value = "session_id", defaultValue = "") String sessionUuid, Model model) {
        String currentSession;
        if (!sessionUuid.equals("")) {
            currentSession = sessionService.findById(sessionUuid).getUser().getLogin();
            model.addAttribute("login", currentSession);
        }

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

        usersService.save(user);
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
                                BindingResult bindingResult, HttpServletResponse response,
                                HttpSession session1,
                                Model model) {
        if (bindingResult.hasErrors())
            return "authorization";

        passwordValidator.validate(user, bindingResult);
        if (bindingResult.hasErrors())
            return "authorization";

        User foundUser = usersService.findByLoginAndPassword(user);
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
    public String findLocations() {
        return "locations";
    }
}

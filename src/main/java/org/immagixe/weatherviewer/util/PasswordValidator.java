package org.immagixe.weatherviewer.util;

import org.immagixe.weatherviewer.models.User;
import org.immagixe.weatherviewer.services.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.validation.Errors;
import org.springframework.validation.Validator;

@Component
public class PasswordValidator implements Validator {

    private final UserService userService;

    @Autowired
    public PasswordValidator(UserService userService) {
        this.userService = userService;
    }

    @Override
    public boolean supports(Class<?> clazz) {
        return User.class.equals(clazz);
    }

    @Override
    public void validate(Object target, Errors errors) {
        User user = (User) target;

        if (userService.findByLoginAndPassword(user) == null) {
            errors.rejectValue("password", "", "Login or password is incorrect");
        }
    }
}

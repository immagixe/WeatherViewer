package org.immagixe.weatherviewer.util;

import org.immagixe.weatherviewer.models.User;
import org.springframework.security.crypto.bcrypt.BCrypt;
import org.springframework.stereotype.Component;

@Component
public class BCryptPassword {

    public static void setSecuredPasswordHash(User user) {
        String originalPassword = user.getPassword();
        String generatedSecuredPasswordHash = BCrypt.hashpw(originalPassword, BCrypt.gensalt(12));
        user.setPassword(generatedSecuredPasswordHash);
    }

     public static boolean checkSecuredPassword (String originalPassword, String securedPasswordHash) {
        return BCrypt.checkpw(originalPassword, securedPasswordHash);
    }
}



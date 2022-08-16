package org.immagixe.weatherviewer.util;

import org.springframework.security.crypto.bcrypt.BCrypt;
import org.springframework.stereotype.Component;

@Component
public class BCryptPassword {

    public static String getSecuredPasswordHash(String originalPassword) {
        return BCrypt.hashpw(originalPassword, BCrypt.gensalt(12));
    }

     public static boolean checkSecuredPassword (String originalPassword, String securedPasswordHash) {
        return BCrypt.checkpw(originalPassword, securedPasswordHash);
    }
}



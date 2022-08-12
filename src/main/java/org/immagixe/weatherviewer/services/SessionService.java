package org.immagixe.weatherviewer.services;

import org.immagixe.weatherviewer.models.Session;
import org.immagixe.weatherviewer.models.User;
import org.immagixe.weatherviewer.repositories.SessionRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.servlet.http.Cookie;
import java.sql.Timestamp;
import java.util.UUID;

@Service
@Transactional(readOnly = true)
public class SessionService {

    public static final long SESSION_LIFE_TIME = 1000 * 60 * 60 * 2;

    private final SessionRepository sessionRepository;

    @Autowired
    public SessionService(SessionRepository sessionRepository) {
        this.sessionRepository = sessionRepository;
    }

    @Transactional
    public UUID saveSessionAndGetUuid(User user) {
        Timestamp expiresAtTime = new Timestamp(System.currentTimeMillis() + SESSION_LIFE_TIME);
        Session session = new Session(user, expiresAtTime);
        sessionRepository.save(session);

        return session.getId();
    }

    public Session findById(String sessionUuid) {
        UUID uuid = UUID.fromString(sessionUuid);
        return sessionRepository.findById(uuid).orElse(null);
    }

    public boolean isExpired(String sessionUuid) {
        if (sessionUuid.equals(""))
            return true;

        Session session = findById(sessionUuid);
        Timestamp expiresAt = session.getExpiresAt();
        Timestamp currentTimestamp = new Timestamp(System.currentTimeMillis());

        return currentTimestamp.after(expiresAt);
    }

    public String getAuthorizedLogin (String sessionUuid) {
        UUID uuid = UUID.fromString(sessionUuid);
        Session session = sessionRepository.findById(uuid).orElse(null);

        assert session != null;
        return session.getUser().getLogin();
    }

    public User getUser (String sessionUuid) {
        Session session = findById(sessionUuid);
        return session.getUser();
    }

    public Cookie cleanCookie () {
        Cookie cookie = new Cookie("session_id", "");
        cookie.setMaxAge(0);
        return cookie;
    }
}

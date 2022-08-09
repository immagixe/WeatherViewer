package org.immagixe.weatherviewer.services;

import org.immagixe.weatherviewer.models.User;
import org.immagixe.weatherviewer.repositories.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Service
@Transactional(readOnly = true)
public class UsersService {

    private final UserRepository userRepository;

    @Autowired
    public UsersService(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    public List<User> findAll () {
        return userRepository.findAll();
    }

    public User findOne(int id) {
         Optional<User> foundUser = userRepository.findById(id);
         return foundUser.orElse(null);
    }

    @Transactional
    public void save (User user) {
        userRepository.save(user);
    }

    public User findByLogin(User user) {
        String login = user.getLogin();
        return userRepository.findByLogin(login).orElse(null);
    }

    public User findByLoginAndPassword(User user) {
        String login = user.getLogin();
        String password = user.getPassword();
        return userRepository.findByLoginAndPassword(login, password).orElse(null);
    }
}



package org.immagixe.weatherviewer.repositories;

import org.immagixe.weatherviewer.models.Location;
import org.immagixe.weatherviewer.models.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface LocationRepository extends JpaRepository<Location, Integer> {

    Optional<Location> findByNameAndUser(String name, User userId);

    Optional<Location> findByUser(User user);
}

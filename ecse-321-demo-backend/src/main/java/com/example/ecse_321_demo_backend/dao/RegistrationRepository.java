package com.example.ecse_321_demo_backend.dao;

import com.example.ecse_321_demo_backend.models.Event;
import com.example.ecse_321_demo_backend.models.Registration;
import com.example.ecse_321_demo_backend.models.UserAccount;
import com.example.ecse_321_demo_backend.models.keys.RegistrationId;
import java.util.List;
import java.util.Optional;
import org.springframework.data.repository.CrudRepository;

public interface RegistrationRepository
    extends CrudRepository<Registration, RegistrationId> {
    Optional<Registration> findById(RegistrationId id);
    List<Registration> findByUser(UserAccount user);
    List<Registration> findByEvent(Event event);
    Optional<Registration> findByUserAndEvent(UserAccount user, Event event);
}

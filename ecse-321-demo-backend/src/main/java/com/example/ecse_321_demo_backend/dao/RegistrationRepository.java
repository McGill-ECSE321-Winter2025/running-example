package com.example.ecse_321_demo_backend.dao;

import org.springframework.data.repository.CrudRepository;
import com.example.ecse_321_demo_backend.models.Registration;
import com.example.ecse_321_demo_backend.models.UserAccount;
import com.example.ecse_321_demo_backend.models.Event;
import com.example.ecse_321_demo_backend.models.keys.RegistrationId;
import java.util.Optional;
import java.util.List;

public interface RegistrationRepository extends CrudRepository<Registration, RegistrationId> {
    Optional<Registration> findById(RegistrationId id);
    List<Registration> findByUser(UserAccount user);
    List<Registration> findByEvent(Event event);
}

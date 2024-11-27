package com.example.ecse_321_demo_backend.dao;

import com.example.ecse_321_demo_backend.models.Location;
import java.util.Optional;
import java.util.UUID;
import org.springframework.data.repository.CrudRepository;
import org.springframework.lang.NonNull;

public interface LocationRepository extends CrudRepository<Location, UUID> {
  @NonNull
  Optional<Location> findById(@NonNull UUID id);
}

package com.gtpuser.gtpuser.repo;

import com.gtpuser.gtpuser.models.User;
import org.springframework.data.repository.reactive.ReactiveCrudRepository;
import org.springframework.stereotype.Repository;

import java.util.UUID;

@Repository
public interface UserRepo extends ReactiveCrudRepository<User, UUID> {
}

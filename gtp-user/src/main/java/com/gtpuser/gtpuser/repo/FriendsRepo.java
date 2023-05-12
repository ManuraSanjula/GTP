package com.gtpuser.gtpuser.repo;

import com.gtpuser.gtpuser.models.Friends;
import org.springframework.data.repository.reactive.ReactiveCrudRepository;
import org.springframework.stereotype.Repository;

import java.util.UUID;

@Repository
public interface FriendsRepo extends ReactiveCrudRepository<Friends, UUID> {
}

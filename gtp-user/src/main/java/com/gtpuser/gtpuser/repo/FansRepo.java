package com.gtpuser.gtpuser.repo;

import com.gtpuser.gtpuser.models.Fans;
import org.springframework.data.repository.reactive.ReactiveCrudRepository;
import org.springframework.stereotype.Repository;

import java.util.UUID;

@Repository
public interface FansRepo extends ReactiveCrudRepository<Fans, UUID> {
}

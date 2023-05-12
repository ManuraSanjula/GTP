package com.gtpuser.gtpuser.repo;

import com.gtpuser.gtpuser.models.FriendReq;
import org.springframework.data.repository.reactive.ReactiveCrudRepository;
import org.springframework.stereotype.Repository;

import java.util.UUID;

@Repository
public interface FriendReqRepo extends ReactiveCrudRepository<FriendReq, UUID> {
}

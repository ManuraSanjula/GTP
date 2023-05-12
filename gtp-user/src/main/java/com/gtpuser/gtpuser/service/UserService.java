package com.gtpuser.gtpuser.service;

import com.gtpuser.gtpuser.Controller.models.UserReq;
import com.gtpuser.gtpuser.Controller.models.UserRes;
import com.gtpuser.gtpuser.kafka.event.UserEvent;
import com.gtpuser.gtpuser.models.User;
import com.gtpuser.gtpuser.repo.*;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.kafka.core.reactive.ReactiveKafkaProducerTemplate;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;
import reactor.core.scheduler.Schedulers;

import java.util.UUID;

@Service
public class UserService {

    @Autowired
    private UserRepo userRepo;
    @Autowired
    private UserForReqRepo userForReqRepo;
    @Autowired
    private FriendReqRepo friendReqRepo;
    @Autowired
    private FriendsRepo friendRepo;
    @Autowired
    private FansRepo fansRepo;

    @Autowired
    private ReactiveKafkaProducerTemplate<String, UserEvent> template;

    public Mono<UserRes> saveUser(UserReq userReq){
        ModelMapper modelMapper = new ModelMapper();
        UUID uuid = UUID.randomUUID();
        User user = modelMapper.map(userReq, User.class);
        user.setId(uuid);
        return userRepo.save(user)
                .map(i->{
                    UserEvent userEvent = new UserEvent(
                            uuid,
                            i.getAccountNonLocked(),
                            i.getAccountNonExpired(),
                            i.getName(),
                            i.getPhoneNumber(),
                            i.getPhoneNumberVerify(),
                            i.getPic(),
                            i.getActive(),
                            i.getRoles(),
                            i.getAuthorities()
                    );
                    template.send("user-created",i.getId().toString(), userEvent).subscribe(

                    );
                    return i;
                })
                .map(i-> modelMapper.map(i, UserRes.class))
                .publishOn(Schedulers.boundedElastic()).subscribeOn(Schedulers.boundedElastic());
    }

    public Mono<UserRes> getUser(UUID id){
        ModelMapper modelMapper = new ModelMapper();
        return userRepo.findById(id).map(i-> modelMapper.map(i, UserRes.class))
                .publishOn(Schedulers.boundedElastic()).subscribeOn(Schedulers.boundedElastic());
    }



}

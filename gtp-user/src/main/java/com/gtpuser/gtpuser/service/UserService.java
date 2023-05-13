package com.gtpuser.gtpuser.service;

import com.gtpuser.gtpuser.Controller.models.UserReq;
import com.gtpuser.gtpuser.Controller.models.UserRes;
import com.gtpuser.gtpuser.error.model.UserError;
import com.gtpuser.gtpuser.error.model.UserNotFoundError;
import com.gtpuser.gtpuser.kafka.event.UserEvent;
import com.gtpuser.gtpuser.models.User;
import com.gtpuser.gtpuser.repo.*;
import com.gtpuser.gtpuser.utils.ErrorMessages;
import com.gtpuser.gtpuser.utils.TokenManager;
import com.nimbusds.jwt.SignedJWT;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.kafka.core.reactive.ReactiveKafkaProducerTemplate;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;
import reactor.core.scheduler.Schedulers;

import java.util.Arrays;
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
    private TokenManager tokenCreator;

    @Autowired
    private ReactiveKafkaProducerTemplate<String, UserEvent> template;

    public Mono<ResponseEntity<UserRes>> saveUser(UserReq userReq){
        ModelMapper modelMapper = new ModelMapper();
        UUID uuid = UUID.randomUUID();
        User user = modelMapper.map(userReq, User.class);
        user.setAuthorities(Arrays.asList("READ_AUTHORITY","WRITE_AUTHORITY","DELETE_AUTHORITY","UPDATE_AUTHORITY"));
        user.setRoles(Arrays.asList("ROLE_USER"));
        user.setId(uuid);

        return userRepo.findByPhoneNumber(userReq.getPhoneNumber()).flatMap(i->{
            if(i == null)
                return getAuthorization(modelMapper, uuid, user)
                        .publishOn(Schedulers.boundedElastic()).subscribeOn(Schedulers.boundedElastic());
            return Mono.error(new UserError(ErrorMessages.RECORD_ALREADY_EXISTS.getErrorMessage()));
        });

    }

    private Mono<ResponseEntity<UserRes>> getAuthorization(ModelMapper modelMapper, UUID uuid, User user) {
        return userRepo.save(user)
                .map(i -> {
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
                    template.send("user-created", i.getId().toString(), userEvent).subscribe(

                    );
                    try {
                        SignedJWT signedJWT = tokenCreator.createSignedJWT(i, i.getPhoneNumber());
                        String encryptToken = tokenCreator.encryptToken(signedJWT);
                        return ResponseEntity.ok()
                                .header("Authorization", encryptToken)
                                .body(modelMapper.map(i, UserRes.class));
                    } catch (Exception e) {
                        return ResponseEntity.ok()
                                .body(modelMapper.map(i, UserRes.class));
                    }
                })
                .publishOn(Schedulers.boundedElastic()).subscribeOn(Schedulers.boundedElastic());
    }

    public Mono<UserRes> getUser(UUID id){
        ModelMapper modelMapper = new ModelMapper();
        return userRepo.findById(id).map(i-> modelMapper.map(i, UserRes.class))
                .publishOn(Schedulers.boundedElastic()).subscribeOn(Schedulers.boundedElastic())
                .switchIfEmpty(Mono.error(new UserNotFoundError(ErrorMessages.NO_RECORD_FOUND.getErrorMessage())));
    }



}

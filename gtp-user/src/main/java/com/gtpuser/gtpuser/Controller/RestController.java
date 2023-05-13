package com.gtpuser.gtpuser.Controller;


import com.gtpuser.gtpuser.Controller.models.UserReq;
import com.gtpuser.gtpuser.Controller.models.UserRes;
import com.gtpuser.gtpuser.error.model.UserError;
import com.gtpuser.gtpuser.service.UserService;
import com.gtpuser.gtpuser.utils.ErrorMessages;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import reactor.core.publisher.Mono;
import reactor.core.scheduler.Schedulers;

import java.util.UUID;

@org.springframework.web.bind.annotation.RestController("/user")
public class RestController {

    @Autowired
    private UserService userService;

    @PostMapping("/user/createNewUser")
    Mono<ResponseEntity<UserRes>> creatingNewAccount(@RequestBody(required = true) Mono<UserReq> userReq){
        return userReq.flatMap(req-> userService.saveUser(req))
                .publishOn(Schedulers.boundedElastic()).subscribeOn(Schedulers.boundedElastic());
    }

    @GetMapping("/user/{id}")
    Mono<UserRes> getUser(@PathVariable String id){
        try{
            return userService.getUser( UUID.fromString(id))
                    .publishOn(Schedulers.boundedElastic()).subscribeOn(Schedulers.boundedElastic());
        }catch (Exception e){
           return Mono.error(new UserError("Bad Request"));
        }
    }
}

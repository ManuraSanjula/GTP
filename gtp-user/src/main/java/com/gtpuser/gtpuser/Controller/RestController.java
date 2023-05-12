package com.gtpuser.gtpuser.Controller;


import com.gtpuser.gtpuser.Controller.models.UserReq;
import com.gtpuser.gtpuser.Controller.models.UserRes;
import com.gtpuser.gtpuser.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import reactor.core.publisher.Mono;
import reactor.core.scheduler.Schedulers;

@org.springframework.web.bind.annotation.RestController("/user")
public class RestController {

    @Autowired
    private UserService userService;

    @PostMapping("/user/createNewUser")
    Mono<ResponseEntity<UserRes>> creatingNewAccount(@RequestBody(required = true) Mono<UserReq> userReq){
        return userReq.flatMap(req-> userService.saveUser(req))
                .publishOn(Schedulers.boundedElastic()).subscribeOn(Schedulers.boundedElastic());
    }
}

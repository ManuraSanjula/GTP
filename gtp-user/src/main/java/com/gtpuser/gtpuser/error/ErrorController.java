package com.gtpuser.gtpuser.error;

import java.util.Date;


import com.gtpuser.gtpuser.error.Res.ErrorMessage;
import com.gtpuser.gtpuser.error.model.UserError;
import com.gtpuser.gtpuser.error.model.UserNotFoundError;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

import reactor.core.publisher.Mono;

@ControllerAdvice
public class ErrorController {

    @ExceptionHandler(UserNotFoundError.class)
    public Mono<ResponseEntity<ErrorMessage>> handleUserNotFoundError(UserNotFoundError ex){

        ErrorMessage errorMessage = new ErrorMessage(new Date(), ex.getMessage());
        return Mono.just(ResponseEntity.status(HttpStatus.NOT_FOUND).body(errorMessage));
    }

    @ExceptionHandler(UserError.class)
    public Mono<ResponseEntity<ErrorMessage>> handleUserError(UserError ex){
        
        ErrorMessage errorMessage = new ErrorMessage(new Date(), ex.getMessage());
        return Mono.just(ResponseEntity.status(HttpStatus.BAD_REQUEST).body(errorMessage));
    }    
    
}

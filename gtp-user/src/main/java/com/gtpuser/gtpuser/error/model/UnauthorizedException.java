package com.gtpuser.gtpuser.error.model;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;


@ResponseStatus(value = HttpStatus.UNAUTHORIZED)
public class UnauthorizedException extends ApiException {
    /**
	 * 
	 */
	private static final long serialVersionUID = -8680203960947950041L;

	public UnauthorizedException(String message) {
        super(message, "UNAUTHORIZED");
    }
}

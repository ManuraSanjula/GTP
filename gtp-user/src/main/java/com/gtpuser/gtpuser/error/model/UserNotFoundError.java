package com.gtpuser.gtpuser.error.model;

public class UserNotFoundError extends RuntimeException {
    /**
	 * 
	 */
	private static final long serialVersionUID = -1923959438688743817L;

	public UserNotFoundError(String message) {
        super(message);
    }
}

package com.gtpuser.gtpuser.error.model;

public class UserServerError extends RuntimeException {
    /**
	 * 
	 */
	private static final long serialVersionUID = -1246018972414794001L;

	public UserServerError(String message) {
        super(message);
    }
}

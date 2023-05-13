package com.gtpuser.gtpuser.error.model;

public class UserError extends RuntimeException {
    /**
	 * 
	 */
	private static final long serialVersionUID = 2337826682204516191L;

	public UserError(String message) {
        super(message);
    }
}

package com.chahar.springmvc.otp.strategy;

/**
 * Implementations of this interface generate random token strings.
 */
public interface OtpGenerator {
	
	/**
	 * Generates a random token.
	 */
	public String generateToken();
}

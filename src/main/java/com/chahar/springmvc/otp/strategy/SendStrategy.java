package com.chahar.springmvc.otp.strategy;

/**
 * Strategy that can be used to send a token to a user.
 */
public interface SendStrategy {
	public void send(String token, String destination);
}

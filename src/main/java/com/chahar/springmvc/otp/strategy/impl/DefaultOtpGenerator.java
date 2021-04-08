package com.chahar.springmvc.otp.strategy.impl;

import com.chahar.springmvc.otp.strategy.OtpGenerator;

import java.security.SecureRandom;
import java.util.Random;

/**
 * Generates random OTP tokens using the letters A-Z and digits 0-9.
 */
public class DefaultOtpGenerator implements OtpGenerator {
	private static final char[] CHARS = {'A', 'B', 'C', 'D', 'E', 'F', 'G', 'H', 'I', 'J', 'K', 'L',
		'M', 'N', 'O', 'P', 'Q', 'R', 'S', 'T', 'U', 'V', 'W', 'X', 'Y', 'Z', '0', '1', '2', '3',
		'4', '5', '6', '7', '8', '9'};
	
	private Random rand = new SecureRandom();
	private int length = 6;
	
	@Override
	public String generateToken() {
		StringBuilder sb = new StringBuilder(length);
		for (int i = 0; i < length; i++) {
			sb.append(CHARS[rand.nextInt(CHARS.length)]);
		}
		return sb.toString();
	}

	public void setLength(int length) {
		this.length = length;
	}

	public void setRand(Random rand) {
		this.rand = rand;
	}
}
